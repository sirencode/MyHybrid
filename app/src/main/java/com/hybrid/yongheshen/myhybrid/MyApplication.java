package com.hybrid.yongheshen.myhybrid;

import android.app.Application;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class MyApplication extends Application
{
    public static final String URL_404 = "file:///android_asset/404.html";
    public static final String BASE_URL = "file:///data/data/com.hybrid.yongheshen.myhybrid/core/index.html";
    @Override
    public void onCreate()
    {
        super.onCreate();
    }
}
