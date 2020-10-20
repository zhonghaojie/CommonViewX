package com.lege.legecommonview.videoplayer.bean;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TiktokBean {
    private String url;

    public TiktokBean(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static ArrayList<TiktokBean> testData(){
        ArrayList<TiktokBean> list = new ArrayList<>();
        String videoInfo1 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f350000bu36burvm87ig5iq5lc0&ratio=720p&line=0";
        String videoInfo2 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300fb30000bu1p23jhe55os3k616p0&ratio=720p&line=0";
        String videoInfo3 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f480000bu60ev3pt1j24o6oqq0g&ratio=720p&line=0";
        String videoInfo4 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f220000bu66t9623gcqc15clgl0&ratio=720p&line=0";
        String videoInfo5 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0d00f4f0000bu5f0jf6j3j3c33r513g&ratio=720p&line=0";
        String videoInfo6 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f360000bu3715vivrp2d8nnvo60&ratio=720p&line=0";
        String videoInfo7 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f9f0000bu420j4gf0e2vj3qo2eg&ratio=720p&line=0";
        String videoInfo8 = "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300f140000bu3qkqrsfrlb9komd7gg&ratio=720p&line=0";
        list.add(new TiktokBean(videoInfo1));
        list.add(new TiktokBean(videoInfo2));
        list.add(new TiktokBean(videoInfo3));
        list.add(new TiktokBean(videoInfo4));
        list.add(new TiktokBean(videoInfo5));
        list.add(new TiktokBean(videoInfo6));
        list.add(new TiktokBean(videoInfo7));
        list.add(new TiktokBean(videoInfo8));
        return list;
    }
}
