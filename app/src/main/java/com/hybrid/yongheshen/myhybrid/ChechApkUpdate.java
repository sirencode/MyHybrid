package com.hybrid.yongheshen.myhybrid;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者： yongheshen on 15/12/17.
 */
public class ChechApkUpdate
{

    private CheckApkUpdateInterface mInterface;

    public void setCheckApkUpdateInterface(CheckApkUpdateInterface checkApkUpdateInterface)
    {
        mInterface = checkApkUpdateInterface;
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
                        mInterface.onCheckApkUpdateSuccess(item);

                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        mInterface.onCheckApkUpdateError();
                    }
                } catch (Exception e)
                {
                    mInterface.onCheckApkUpdateError();
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
            String apkVersion = jsonObject.getString("APKVersion");
            int updateType = jsonObject.getInt("updateType");
            String path = jsonObject.getString("apkPath");
            System.out.println("apkVersion=:" + apkVersion + "，updateType=：" + updateType + ",apkpath=:" + path);
            if (!apkVersion.equals(mApkVersion))
            {
                isUpdateApk = true;
            }
            item.setVersion(mApkVersion);
            item.setType(updateType);
            item.setIsUpdate(isUpdateApk);
            item.setPath(path);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return item;
    }

    interface CheckApkUpdateInterface
    {
        abstract void onCheckApkUpdateSuccess(ApkUpdateItem item);
        abstract void onCheckApkUpdateError();
    }
}
