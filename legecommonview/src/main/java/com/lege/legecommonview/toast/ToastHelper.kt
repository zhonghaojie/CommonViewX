package com.lege.legecommonview.toast

import android.view.View
import com.hjq.toast.ToastUtils

/**
 * Description:
 * Created by loctek on 2021/8/20.
 */
fun android.support.v4.app.Fragment.toast(msg: String) {
    activity?.let {
        if (!it.isFinishing) {
            ToastUtils.show(msg)
        }
    }
}
fun android.support.v4.app.Fragment.toast(layoutID:Int,init:(v: View)->Unit) {
    activity?.let {
        if (!it.isFinishing) {
            ToastUtils.setView(layoutID)
            init(ToastUtils.getView())
            ToastUtils.getToast().show()
        }
    }
}
fun android.support.v4.app.Fragment.toast(v: View, init:(v: View)->Unit) {
    activity?.let {
        if (!it.isFinishing) {
            ToastUtils.setView(v)
            init(ToastUtils.getView())
            ToastUtils.getToast().show()
        }
    }
}