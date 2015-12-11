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
    private Thread mUnZipThread, mJumpThread;

    private List<WebZipItem> webZipItems;

    private String PAGNAME = "";

    private Context mContext;

    private OnInitDoneInterface initDoneInterface;

    //TODO

    /**
     * 1 解析配置文件 xml
     * 2 解压本地h5资源包到指定文件夹
     * 3 请求APK升级接口
     * 4 请求H5升级接口
     */

    public InitFramwork(Context context)
    {
        this.mContext = context;
        PAGNAME = context.getPackageName();
    }

    public void init()
    {
        unZipRes();
        jumpToMain();
    }

    public void setInitDoneInterface(OnInitDoneInterface onInitDoneInterface)
    {
        this.initDoneInterface = onInitDoneInterface;

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
                initDoneInterface.doneInit();
            }
        });
        mJumpThread.start();

    }

    /**
     * 将资源包解压到data/data/<包名> 的根目录下
     * <p/>
     * 获取到配置文件中声明的所有的离线zip资源包
     */
    private void unZipRes()
    {
        webZipItems = new ArrayList<>();
        //解析配置文件config.xml
        webZipItems = XmlPullParserUtils.getWebZipItem(mContext);
        mUnZipThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                for (int i = 0; i < webZipItems.size(); i++)
                {
                    InputStream inputStream = mContext.getClass().getClassLoader().getResourceAsStream("assets/" + webZipItems.get(i).getPath());
                    if (inputStream != null)
                    {
                        //资源包MD5校验
                        System.out.println("MD5= :" + FileToMD5.md5sum(mContext.getClass().getClassLoader().getResourceAsStream("assets/core" + webZipItems.get(i).getPath())));
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
