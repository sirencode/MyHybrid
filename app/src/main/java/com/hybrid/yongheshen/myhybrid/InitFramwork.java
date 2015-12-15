package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.content.Intent;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/11.
 */
public class InitFramwork
{
    private Thread mUnZipThread, mCheckUpdateThread;

    private List<WebZipItem> webZipItems;

    private String PAGNAME = "";

    private Activity mContext;

    public InitFramwork(Activity context)
    {
        this.mContext = context;
        PAGNAME = context.getPackageName();
    }

    public void init()
    {
        unZipRes();
        checkUpdate("https://www.baidu.com");
    }

    private void checkUpdate(final String urlPath)
    {
        mCheckUpdateThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    mUnZipThread.join();
                    // 根据地址创建URL对象(网络访问的url)
                    URL url = new URL(urlPath);
                    // url.openConnection()打开网络链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");// 设置请求的方式
                    urlConnection.setReadTimeout(5000);// 设置超时的时间
                    urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
                    // 设置请求的头
                    urlConnection
                            .setRequestProperty("User-Agent",
                                    "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                    // 获取响应的状态码 404 200 505 302
                    if (urlConnection.getResponseCode() == 200)
                    {
                        // 获取响应的输入流对象
                        InputStream is = urlConnection.getInputStream();

                        // 创建字节输出流对象
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        // 定义读取的长度
                        int len = 0;
                        // 定义缓冲区
                        byte buffer[] = new byte[1024];
                        // 按照缓冲区的大小，循环读取
                        while ((len = is.read(buffer)) != -1)
                        {
                            // 根据读取的长度写入到os对象中
                            os.write(buffer, 0, len);
                        }
                        // 释放资源
                        is.close();
                        os.close();
                        // 返回字符串
                        String result = new String(os.toByteArray());
                        System.out.println("***************" + result
                                + "******************");
                        Thread.sleep(1000);
                        Intent intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                        mContext.finish();
                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        onError("网络连接失败");
                    }
                } catch (Exception e)
                {
                    onError("网络连接异常，请检查网络");
                    e.printStackTrace();
                }
            }
        }

        );
        mCheckUpdateThread.start();
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
                        System.out.println("MD5= :" + FileToMD5.md5sum(mContext.getClass().getClassLoader().getResourceAsStream("assets/" + webZipItems.get(i).getPath())));
                        String desPath = "data/data/" + PAGNAME + "/";
                        //将H5资源包解压到指定目录
                        UnZipUtil.Unzip(inputStream, desPath);
                    } else
                    {
                        onError("H5资源被篡改");
                    }
                }
            }
        });
        mUnZipThread.start();
    }

    public boolean isUpdateAPK()
    {
        return false;
    }

    public boolean isUpdateZIPS()
    {
        return false;
    }

    /**
     * 异常信息处理
     *
     * @param info
     */
    private void onError(final String info)
    {
        mContext.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                ToastUtil.showToast(mContext, info);
            }
        });
    }
}
