package com.lege.legecommonview.loading;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.android.exoplayer2.ExoPlayerFactory;


/**
 * Created by zhoushaoqing on 18-11-28.
 */

public class DimenUtil {
    public static int getScreenWidth(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        final Resources resources =  context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

}
