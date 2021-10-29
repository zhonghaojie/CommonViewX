package com.lege.legecommonview.videoplayer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import com.lege.legecommonview.R
import com.lege.legecommonview.videoplayer.util.DensityUtils
import com.lege.legecommonview.videoplayer.util.ScreenUtils
import com.lege.legecommonview.videoplayer.util.TimeUtil
import kotlinx.android.synthetic.main.video_error.view.*
import kotlinx.android.synthetic.main.video_layout.view.*
import java.lang.ref.WeakReference


/**
 * Description:
 * Created by loctek on 2020/10/20.
 */
class TikTokVideoPlayer(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    interface OnProgressChangedListener {
        fun onProgressChanged(progress: Int)
    }

    companion object {
        private const val UPDATE_PROGRESS = 1
    }

    enum class VideoState {
        unKnow, loadFinish, playing, playEnd, pause, error_server, error_unknown
    }

    constructor(context: Context) : this(context, null, -1)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

    private lateinit var videoLayout: View

    init {
        initView()
    }

    private var mVideoState = VideoState.unKnow
    private var mDuration = 0L
    private var mContext: Context? = null
    private var pausePosition = 0
    private var mOnProgressChangedListener: OnProgressChangedListener? = null
    private var mHandler: Handler? = null
    private val seekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar,
            progress: Int,
            fromUser: Boolean
        ) {
            mOnProgressChangedListener?.onProgressChanged(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            Toast.makeText(context, "onStartTrackingTouch", Toast.LENGTH_SHORT).show()
            // 暂停刷新
            mHandler?.removeMessages(UPDATE_PROGRESS)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            Toast.makeText(context, "onStopTrackingTouch", Toast.LENGTH_SHORT).show()
            val progress = seekBar.progress
            if (videoLayout.my_video_view != null) {
                if (progress + 1000 < mDuration) {
                    // 设置当前播放的位置
                    videoLayout.my_video_view.seekTo(progress)
                } else {
                    mVideoState = VideoState.playEnd
                    videoLayout.my_video_view.seekTo(0)
                    start()
                }
                mHandler?.sendEmptyMessage(UPDATE_PROGRESS)
            }
        }
    }

    private fun initView() {
        videoLayout = LayoutInflater.from(context).inflate(R.layout.video_layout, this, true)
        videoLayout.loading_view.visibility = View.VISIBLE
        videoLayout.loading_view.start()
        videoLayout.my_video_view.setOnClickListener {
            if (videoLayout.my_video_view.isPlaying) {
                pause()
            } else {
                start()
            }
        }
        videoLayout.my_video_view.setOnPreparedListener(OnPreparedListener { mp: MediaPlayer ->
            mVideoState = VideoState.loadFinish
            mp.setOnInfoListener { mp1: MediaPlayer?, what: Int, extra: Int ->
                when(what){
                    MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START->{
                        Log.i("Video","MEDIA_INFO_VIDEO_RENDERING_START")
                        //隐藏图片
                        videoLayout.loading_view.stop()
                        videoLayout.loading_view.setVisibility(GONE)
                        mp.isLooping = true
                        if (mHandler != null) {
                            mHandler?.sendEmptyMessage(UPDATE_PROGRESS)
                        }
                    }
                }
                //这里是视频真正开始播放的时候
                true
            }
        })
        videoLayout.my_video_view.setOnCompletionListener { mp: MediaPlayer? ->
            mVideoState = VideoState.playEnd
            mOnProgressChangedListener?.onProgressChanged(mDuration.toInt())
        }
        videoLayout.my_video_view.setOnErrorListener { mp: MediaPlayer?, what: Int, extra: Int ->
            //异常回调
            val errorMsg = if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                //这才是  网络服务错误
                mVideoState = VideoState.error_server
                "网络错误"
            } else {
                //如果是1的话是未知错误
                mVideoState = VideoState.error_unknown
                "未知错误"
            }
            videoLayout.error_layout.setVisibility(VISIBLE)
            videoLayout.tv_error_info.text = errorMsg
            true
        }
        videoLayout.seek_bar_progress.setOnSeekBarChangeListener(seekBarChangeListener)
        videoLayout.error_btn.setOnClickListener {
            start()
        }
    }

    fun start() {
        if (mVideoState == VideoState.playEnd || mVideoState == VideoState.error_server) {
            videoLayout.my_video_view.resume()
            videoLayout.seek_bar_progress.progress = 0
        } else {
            videoLayout.my_video_view.start()
        }
        videoLayout.error_layout.setVisibility(GONE)
        mVideoState = VideoState.playing
        mHandler?.sendEmptyMessage(UPDATE_PROGRESS)
    }

    fun pause() {
        videoLayout.my_video_view.pause()
        mVideoState = VideoState.pause
        pausePosition = videoLayout.my_video_view.currentPosition
    }

    /**
     * 播放器重新可见时调用
     */
    fun resume() {
        videoLayout.my_video_view.resume()
        videoLayout.my_video_view.seekTo(pausePosition)
        videoLayout.seek_bar_progress.setProgress(pausePosition)
        videoLayout.my_video_view.start()
        mHandler?.sendEmptyMessage(UPDATE_PROGRESS)
    }

    /**
     * 播放器不可见时调用
     */
    fun stop() {
        mVideoState = VideoState.playEnd
        videoLayout.my_video_view.stopPlayback()
        videoLayout.seek_bar_progress.setProgress(0)
        videoLayout.loading_view.stop()
        videoLayout.loading_view.visibility = View.GONE
        mHandler?.removeMessages(UPDATE_PROGRESS)
    }


    fun getState(): VideoState? {
        if (videoLayout.my_video_view.isPlaying()) {
            mVideoState = VideoState.playing
        }
        return mVideoState
    }

    fun getCurrentPosition(): Int {
        return videoLayout.my_video_view.getCurrentPosition()
    }

    fun getBufferPercentage(): Int {
        return videoLayout.my_video_view.getBufferPercentage()
    }

    fun setVideoUrl(url: String) {

        val mmr = MediaMetadataRetriever()
        var headers: HashMap<String?, String?>? = null
        if (headers == null) {
            headers = HashMap()
        }
        mmr.setDataSource(url, headers)
        val duration =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "0"//宽
        val height =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "0" //高
        val firstFrame = mmr.getFrameAtTime(0)
        videoLayout.iv_thumb.setImageBitmap(firstFrame)
        videoLayout.my_video_view.setVideoURI(Uri.parse(url))
        mDuration = duration
        videoLayout.seek_bar_progress.setMax(mDuration.toInt())
        if (mHandler == null) {
            mHandler = MyHandler(videoLayout, mDuration)
        }
        //宽高比
        val aspectRatio = width.toFloat() / height.toFloat()
        val layoutParamsVideo = videoLayout.my_video_view.getLayoutParams() as LayoutParams
        setLayoutParam(layoutParamsVideo, aspectRatio)
        videoLayout.my_video_view.setLayoutParams(layoutParamsVideo)
        start()
    }


    private fun setLayoutParam(layoutParams: LayoutParams, aspectRatio: Float) {
        if (aspectRatio == 1f) {
            layoutParams.topMargin = DensityUtils.dp2px(mContext, 105f)
        } else if (aspectRatio > 1) {
            layoutParams.topMargin = DensityUtils.dp2px(mContext, 25f)
        } else {
            layoutParams.topMargin = 0
        }
        layoutParams.height = (ScreenUtils.getScreenWidth(context) / aspectRatio).toInt()
    }

    private class MyHandler constructor(rootView: View, private val duration: Long) : Handler() {
        private var mVideoViewWeakReference: WeakReference<View> = WeakReference(rootView)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val rootView = mVideoViewWeakReference.get() ?: return
            val pausePosition = rootView.my_video_view.currentPosition
            if (msg.what == UPDATE_PROGRESS) {
                if (pausePosition > duration) {
                    rootView.my_video_view.seekTo(0)
                    rootView.seek_bar_progress.progress = 0
                } else {
                    sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000)
                    if (pausePosition > 0) {
                        rootView.seek_bar_progress.progress = pausePosition
                    }
                }
            }
        }

    }


}