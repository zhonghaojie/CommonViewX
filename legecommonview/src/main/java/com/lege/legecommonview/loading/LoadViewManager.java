package com.lege.legecommonview.loading;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.lege.legecommonview.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by zhoushaoqing on 18-11-28.
 */

public class LoadViewManager {
    public static String defalutLoadType = LoadType.BallSpinFadeLoaderIndicator.name();
    public static String NODISMISS_TYPE = "visible";

    public static ArrayList<Dialog> DIALOG_MAP = new ArrayList<>();

    private static int SCALE = 8;
    private static WeakReference<Activity> mContext = null;

    public static void showLoadDiag(Context context, String type) {
        mContext = new WeakReference<>((Activity)context);
        Dialog dialog = new Dialog(mContext.get(), R.style.dialogStyle);
        AVLoadingIndicatorView avLoadingIndicatorView = LoaderCreater.create(context, defalutLoadType);
        dialog.setContentView(avLoadingIndicatorView);
        Window wm = dialog.getWindow();
        if (wm != null) {
            int screenWidth = DimenUtil.getScreenWidth(context);
            int scrrenHight = DimenUtil.getScreenHeight(context);
            WindowManager.LayoutParams wl = wm.getAttributes();
            wl.width = screenWidth / SCALE;
            wl.height = scrrenHight / SCALE + scrrenHight / 10;
            wl.gravity = Gravity.CENTER;
        }
        DIALOG_MAP.add(dialog);
        if (!mContext.get().isFinishing() && !mContext.get().isDestroyed()){
            dialog.show();
        }
        if(type.equals("visible")){
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        }
    }

    public  static  void stopLoading(){
        try {
            if (DIALOG_MAP.size()>0){
                for (int i = 0;i<DIALOG_MAP.size();i++){
                    Dialog diag = DIALOG_MAP.get(i);
                    if(diag != null && diag.isShowing()){
                        diag.cancel();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
