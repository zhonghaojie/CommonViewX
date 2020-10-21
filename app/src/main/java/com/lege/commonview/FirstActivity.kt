package com.lege.commonview

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        startActivity(Intent(this,TikTokActivity::class.java))
    }
}
