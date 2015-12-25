package com.hybrid.yongheshen.myhybrid;

import android.app.Application;

/**
 * 作者： shenyonghe689 on 15/12/10.
 */
public class MyApplication extends Application
{
    public static final String URL_404 = "file:///android_asset/404.html";

    public static  String BASE_URL;

    @Override
    public void onCreate()
    {
        BASE_URL = "file:///data/data/"+this.getPackageName()+"/webroot/core/index.html";
        super.onCreate();
    }
}
