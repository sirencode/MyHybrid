package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.InputStream;

/**
 * 作者： yongheshen on 15/12/8.
 * 描述：
 */
public class WelcomeActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        onDelay();
        //TODO
        /**
         * 1 解析配置文件 xml
         * 2 解压本地h5资源包到指定文件夹
         * 3 请求APK升级接口
         * 4 请求H5升级接口
         */
    }

    private void onDelay()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                //解析配置文件config.xml
                XmlPullParserUtils.getWebZipItem(getApplicationContext());
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/core/core.zip");
                //资源包MD5校验
                System.out.println("MD5= :"+FileToMD5.md5sum(this.getClass().getClassLoader().getResourceAsStream("assets/core/core.zip")));
                String desPath = "data/data/com.hybrid.yongheshen.myhybrid/";
                //将H5资源包解压到指定目录
                UnZipUtil.Unzip(inputStream, desPath);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }).start();

    }

}
