package com.lege.commonview

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dueeeke.videocontroller.StandardVideoController
import com.lege.legecommonview.videoplayer.TikTokVideoPlayer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var list = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mActionBar = supportActionBar
        mActionBar?.hide()
        val decorView = window.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                //                 | View.SYSTEM_UI_FLAG_FULLSCREEN       // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        systemUiVisibility = systemUiVisibility or flags
        decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_main)
        setUrl()
    }
    private fun setUrl(){
        val videoInfo1 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f350000bu36burvm87ig5iq5lc0&ratio=720p&line=0"
        val videoInfo2 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300fb30000bu1p23jhe55os3k616p0&ratio=720p&line=0"
        val videoInfo3 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f480000bu60ev3pt1j24o6oqq0g&ratio=720p&line=0"
        val videoInfo4 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f220000bu66t9623gcqc15clgl0&ratio=720p&line=0"
        val videoInfo5 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0d00f4f0000bu5f0jf6j3j3c33r513g&ratio=720p&line=0"
        val videoInfo6 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f360000bu3715vivrp2d8nnvo60&ratio=720p&line=0"
        val videoInfo7 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f9f0000bu420j4gf0e2vj3qo2eg&ratio=720p&line=0"
        val videoInfo8 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f140000bu3qkqrsfrlb9komd7gg&ratio=720p&line=0"
        list.add(videoInfo1)
        list.add(videoInfo2)
        list.add(videoInfo3)
        list.add(videoInfo4)
        list.add(videoInfo5)
        list.add(videoInfo6)
        list.add(videoInfo7)
        list.add(videoInfo8)
        recyclerview.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview)
        recyclerview.adapter = object:
            androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(){
            private val inflater:LayoutInflater = LayoutInflater.from(this@MainActivity)
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
                val holder = object: androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.item_video_player,parent,false)){}
                return holder
            }

            override fun getItemCount(): Int {
                return list.size
            }

            override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
                holder.itemView.findViewById<TikTokVideoPlayer>(R.id.video_player).setVideoUrl(list[position])
            }

            override fun onViewRecycled(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
                super.onViewRecycled(holder)

            }
        }
    }
}
