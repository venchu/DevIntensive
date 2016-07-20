package com.softdesign.devintensive.network.interceptors;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class PicassoCache {
    private Context mContext;
    private Picasso mPicassoInstance;

    public PicassoCache(Context context) {
        this.mContext = context;
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(okHttp3Downloader);

        mPicassoInstance = builder.build();
        Picasso.setSingletonInstance(mPicassoInstance);
    }

    public Picasso getmPicassoInstance(){
        if(mPicassoInstance == null) {
            new PicassoCache(mContext);
        }

        return mPicassoInstance;
    }
}