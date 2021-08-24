package com.lege.commonview

import android.app.Application
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory
import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.lege.legecommonview.toast.ToastHelp

/**
 * Description:
 * Created by loctek on 2021/3/11.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastHelp.initToastUtil(this)
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
                .setPlayerFactory(IjkPlayerFactory.create())
                //使用ExoPlayer解码
//            .setPlayerFactory(ExoMediaPlayerFactory.create())
                //使用MediaPlayer解码
//            .setPlayerFactory(AndroidMediaPlayerFactory.create())
                .build()
        )
    }
}