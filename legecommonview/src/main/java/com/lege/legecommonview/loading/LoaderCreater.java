package com.lege.legecommonview.loading;

import android.content.Context;

import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.Indicator;

import java.util.WeakHashMap;

/**
 * Created by zhoushaoqing on 18-11-28.
 */

public class LoaderCreater {


    static final WeakHashMap<String, Indicator> LOAD_MAP = new WeakHashMap<>();

    public static AVLoadingIndicatorView create(Context context, String type) {
        AVLoadingIndicatorView avLoadingIndicatorView = new AVLoadingIndicatorView(context);
        Indicator indicator = null;
        if (type == null || type.isEmpty()) {
            return null;
        } else {
            if (LOAD_MAP.containsKey(type)) {
                indicator = LOAD_MAP.get(type);
            } else {
                indicator = getIndicator(type);
                LOAD_MAP.put(type, indicator);
            }
        }
        avLoadingIndicatorView.setIndicator(indicator);

        return avLoadingIndicatorView;
    }


    public static Indicator getIndicator(String type) {
        StringBuilder className = new StringBuilder();
        if(!type.contains(".")){
            String defaultPackageName =  AVLoadingIndicatorView.class.getPackage().getName();
            className.append(defaultPackageName);
            className.append(".indicators");
            className.append(".");
        }
        className.append(type);

        try {
            Class<?> name = Class.forName(className.toString());
            return (Indicator) name.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }
}
