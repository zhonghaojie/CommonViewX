package com.lege.legecommonview.videoplayer.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lege.legecommonview.R;
import com.lege.legecommonview.TikTokView;
import com.lege.legecommonview.videoplayer.bean.TiktokBean;
import com.lege.legecommonview.videoplayer.cache.PreloadManager;

import java.util.ArrayList;
import java.util.List;

public class Tiktok2Adapter<T extends IVideoContentProvider> extends PagerAdapter {
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    /**
     * View缓存池，从ViewPager中移除的item将会存到这里面，用来复用
     */
    private List<View> mViewPool = new ArrayList<>();

    /**
     * 数据源
     */
    private List<T> mVideoBeans;

    public Tiktok2Adapter(List<T> videoBeans) {
        this.mVideoBeans = videoBeans;
    }

    @Override
    public int getCount() {
        return mVideoBeans == null ? 0 : mVideoBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        View view = null;
        if (mViewPool.size() > 0) {//取第一个进行复用
            view = mViewPool.get(0);
            mViewPool.remove(0);
        }

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_tik_tok, container, false);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        IVideoContentProvider item = mVideoBeans.get(position);
        //开始预加载
        PreloadManager.getInstance(context).addPreloadTask(item.getUrl(), position);

        viewHolder.mPosition = position;
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View itemView = (View) object;
        container.removeView(itemView);
        IVideoContentProvider item = mVideoBeans.get(position);
        //取消预加载
        PreloadManager.getInstance(container.getContext()).removePreloadTask(item.getUrl());
        //保存起来用来复用
        mViewPool.add(itemView);
    }

    /**
     * 借鉴ListView item复用方法
     */
    public static class ViewHolder {

        public int mPosition;
        public ImageView mThumb;//封面图
        public TikTokView mTikTokView;
        public FrameLayout mPlayerContainer;

        ViewHolder(View itemView) {
            mTikTokView = itemView.findViewById(R.id.tiktok_View);
            mThumb = mTikTokView.findViewById(R.id.iv_thumb);
            mPlayerContainer = itemView.findViewById(R.id.container);
            itemView.setTag(this);
        }
    }
}
