package com.lege.commonview

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.widget.SeekBar
import com.lege.commonview.tarottest.TarotActivity
import com.lege.legecommonview.toast.ToastHelp
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

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
        setContentView(R.layout.activity_first)
//        startActivity(Intent(this,TikTokActivity::class.java))
        initView()
    }

    private fun initView() {
        btn_reset.setOnClickListener {
            seekbarX.progress = 50
            seekbarY.progress = 50
            seekbarZ.progress = 50
//            ToastUtils.show("吐司")
            ToastHelp.toastWithIcon("这是一个Toast")
        }
        seekbarX.setOnSeekBarChangeListener(this)
        seekbarY.setOnSeekBarChangeListener(this)
        seekbarZ.setOnSeekBarChangeListener(this)
        card.postDelayed({
            card.rotateX = -60
        },2000)

        btn_next.setOnClickListener {
            startActivity(Intent(this,TarotActivity::class.java))
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar?.let {
            val rotate = 360 * (progress - 50) / 50
            when (it.id) {
                R.id.seekbarX -> {
                    card.rotateX = rotate
                }
                R.id.seekbarY -> {
                    card.rotateY = rotate
                }
                R.id.seekbarZ -> {
                    card.rotateZ = rotate
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.let {
            val rotate = 360 * (it.progress - 50) / 50
            when (it.id) {
                R.id.seekbarX -> {
                    card.rotateX = rotate
                }
                R.id.seekbarY -> {
                    card.rotateY = rotate
                }
                R.id.seekbarZ -> {
                    card.rotateZ = rotate
                }
            }
        }
    }
}
