package com.hybrid.yongheshen.myhybrid;

import android.content.Context;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/11.
 */
public class InitFramwork
{
    private static Thread mUnZipThread, mJumpThread;

    private static List<WebZipItem> webZipItems;

    private static final int NO_ZIPRESOURE = 0x0001;

    private static String PAGNAME = "";

    private static OnInitDoneInterface initDoneInterface;

    public static void init(Context context)
    {
        unZipRes(context);
    }

    public static void setInitDoneInterface(OnInitDoneInterface onInitDoneInterface)
    {
        InitFramwork.initDoneInterface = onInitDoneInterface;
        jumpToMain();
    }

    private static void jumpToMain()
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
                InitFramwork.initDoneInterface.doneInit();
            }
        });
        mJumpThread.start();

    }

    /**
     * 将资源包解压到data/data/<包名> 的根目录下
     * <p/>
     * 获取到配置文件中声明的所有的离线zip资源包
     */
    private static void unZipRes(final Context context)
    {
        webZipItems = new ArrayList<>();
        //解析配置文件config.xml
        webZipItems = XmlPullParserUtils.getWebZipItem(context);
        mUnZipThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                for (int i = 0; i < webZipItems.size(); i++)
                {
                    InputStream inputStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + webZipItems.get(i).getPath());
                    if (inputStream != null)
                    {
                        //资源包MD5校验
                        System.out.println("MD5= :" + FileToMD5.md5sum(this.getClass().getClassLoader().getResourceAsStream("assets/core" + webZipItems.get(i).getPath())));
                        String desPath = "data/data/" + PAGNAME + "/";
                        //将H5资源包解压到指定目录
                        UnZipUtil.Unzip(inputStream, desPath);
                    } else
                    {
//                        Message message = Message.obtain(mHandler);
//                        message.what = NO_ZIPRESOURE;
//                        mHandler.sendMessage(message);
                    }
                }
            }
        });
        mUnZipThread.start();
    }

    private void checkUpdate()
    {

    }

    public interface OnInitDoneInterface
    {
        public abstract void doneInit();
    }
}
