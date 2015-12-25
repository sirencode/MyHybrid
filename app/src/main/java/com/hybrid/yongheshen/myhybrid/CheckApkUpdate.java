package com.hybrid.yongheshen.myhybrid;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者： shenyonghe689 on 15/12/17.
 */
public class CheckApkUpdate
{

    private Handler mHandler;
    private CheckH5Update  mCheckH5Update;

    public CheckApkUpdate(Handler handler, CheckH5Update checkH5Update){
        this.mHandler = handler;
        this.mCheckH5Update = checkH5Update;
    }

    public void checkApkUpdate(final String urlPath, final String apkVersion)
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
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
                        ApkUpdateItem item = parserApkUpdateJson(result, apkVersion);
                        onCheckApkUpdateSuccess(item);

                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        onCheckApkUpdateError();
                    }
                } catch (Exception e)
                {
                    onCheckApkUpdateError();
                    System.out.println("网络异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

    /**
     * 获取APK升级信息
     *
     * @param updateInfo
     * @return
     */
    private ApkUpdateItem parserApkUpdateJson(String updateInfo, String mApkVersion)
    {
        ApkUpdateItem item = new ApkUpdateItem();
        boolean isUpdateApk = false;
        JSONTokener jsonTokener = new JSONTokener(updateInfo);
        try
        {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            String lastAppVersion = jsonObject.getString("lastAppVersion");
            String updateFlag = jsonObject.getString("updateFlag");
            String updateMsg = jsonObject.getString("updateMsg");
            String appDownLoadUrl = jsonObject.getString("appDownLoadUrl");
            String appSize = jsonObject.getString("appSize");
            String appName = jsonObject.getString("appName");
            if (!lastAppVersion.equals(mApkVersion))
            {
                isUpdateApk = true;
            }
            item.setLastAppVersion(lastAppVersion);
            item.setUpdateMsg(updateMsg);
            item.setIsUpdate(isUpdateApk);
            item.setAppDownLoadUrl(appDownLoadUrl);
            item.setAppName(appName);
            item.setUpdateFlag(updateFlag);
            item.setAppSize(appSize);
            MyHybridConfig.ApkUpdateItem = item;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return item;
    }

    private void onCheckApkUpdateSuccess(ApkUpdateItem item)
    {
        if ((item != null) && item.isUpdate())
        {
            if (item.getUpdateFlag().equals("2"))
            {
                mHandler.sendEmptyMessage(MyHybridConfig.APK_FOCE_SHOW);
            } else if(item.getUpdateFlag().equals("1"))
            {
                mHandler.sendEmptyMessage(MyHybridConfig.APK_NORMAL_SHOW);
            }else
            {
                System.out.println("未知的升级标志");
            }
        } else
        {
            mCheckH5Update.checkApkUpdate(MyHybridConfig.H5UpdateUrl, MyHybridConfig.WebZipItems);
        }
    }

    private void onCheckApkUpdateError()
    {
        mHandler.sendEmptyMessage(MyHybridConfig.ALERT_NETERROR);
    }


}
