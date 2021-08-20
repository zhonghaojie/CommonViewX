package com.lege.legecommonview.toast

import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.ToastBlackStyle
import com.lege.legecommonview.R

/**
 * Description:
 * Created by loctek on 2021/8/20.
 */
object ToastHelp {
    fun initToastUtil(app:Application,style: ToastBlackStyle = MyToastBlackStyle(app)){
        ToastUtils.init(app,style)
    }
    fun toastWithIcon(msg: String, icon: Int = R.drawable.ico_tishi) {
        ToastUtils.setView(R.layout.layout_toast_with_icon)
        ToastUtils.getView<View>().findViewById<TextView>(R.id.tv_toast_content)?.text = msg
        ToastUtils.getView<View>().findViewById<ImageView>(R.id.iv_toast_icon)
            ?.setImageResource(icon)
        ToastUtils.show(msg)
    }

    fun toastWithoutIcon(msg: String) {
        ToastUtils.setView(R.layout.layout_toast_without_icon)
        ToastUtils.getView<View>().findViewById<TextView>(R.id.tv_toast_content)?.text = msg
        ToastUtils.show(msg)
    }

    fun toastWithIcon(msg: Int, icon: Int = R.drawable.ico_tishi) {
        ToastUtils.setView(R.layout.layout_toast_with_icon)
        ToastUtils.getView<View>().findViewById<TextView>(R.id.tv_toast_content)?.setText(msg)
        ToastUtils.getView<View>().findViewById<ImageView>(R.id.iv_toast_icon)
            ?.setImageResource(icon)
        ToastUtils.show(msg)
    }

    fun toastWithoutIcon(msg: Int) {
        ToastUtils.setView(R.layout.layout_toast_without_icon)
        ToastUtils.getView<View>().findViewById<TextView>(R.id.tv_toast_content)?.setText(msg)
        ToastUtils.show(msg)
    }
}