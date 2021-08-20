package com.lege.legecommonview.toast;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.hjq.toast.style.ToastBlackStyle;

/**
 * Description:
 * Created by loctek on 2021/8/20.
 */
public class MyToastBlackStyle extends ToastBlackStyle {

    public MyToastBlackStyle(Context context) {
        super(context);
    }

    @Override
    public int getBackgroundColor() {
        return Color.parseColor("#d937393d");
    }

    @Override
    public int getPaddingTop() {
        return 16;
    }

    @Override
    public int getPaddingStart() {
        return 20;
    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public float getTextSize() {
        return 28;
    }

    @Override
    public int getCornerRadius() {
        return 6;
    }


    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }
}
