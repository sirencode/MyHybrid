package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/8.
 * 描述：
 */
public class WelcomeActivity extends Activity
{
    private Thread mUnZipThread, mJumpThread;

    private List<WebZipItem> webZipItems;

    private static final int NO_ZIPRESOURE = 0x0001;

    private static String PAGNAME = "";

    private android.os.Handler mHandler = new android.os.Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case NO_ZIPRESOURE:
                    Toast.makeText(getApplicationContext(), "WEB资源包未找到！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        PAGNAME = getPackageName();
        webZipItems = new ArrayList<>();
        //解析配置文件config.xml
        webZipItems = XmlPullParserUtils.getWebZipItem(getApplicationContext());
        unZipRes(webZipItems);
        checkUpdate();
        jumpToMain();
        //TODO
        /**
         * 1 解析配置文件 xml
         * 2 解压本地h5资源包到指定文件夹
         * 3 请求APK升级接口
         * 4 请求H5升级接口
         */
    }

    private void jumpToMain()
    {

        mJumpThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    mUnZipThread.join();
                    if (mUnZipThread != null)
                    {
                        mUnZipThread = null;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });
        mJumpThread.start();

    }

    /**
     * 将资源包解压到data/data/<包名> 的根目录下
     *
     * @param list 获取到配置文件中声明的所有的离线zip资源包
     */
    private void unZipRes(final List<WebZipItem> list)
    {
        mUnZipThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                for (int i = 0; i < list.size(); i++)
                {
                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/" + list.get(i).getPath());
                    if (inputStream != null)
                    {
                        //资源包MD5校验
                        System.out.println("MD5= :" + FileToMD5.md5sum(this.getClass().getClassLoader().getResourceAsStream("assets/" + list.get(i).getPath())));
                        String desPath = "data/data/" + PAGNAME + "/";
                        //将H5资源包解压到指定目录
                        UnZipUtil.Unzip(inputStream, desPath);
                    } else
                    {
                        Message message = Message.obtain(mHandler);
                        message.what = NO_ZIPRESOURE;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
        mUnZipThread.start();
    }

    private void checkUpdate()
    {

    }

}

