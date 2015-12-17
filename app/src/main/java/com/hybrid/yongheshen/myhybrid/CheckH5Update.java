package com.hybrid.yongheshen.myhybrid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/17.
 */
public class CheckH5Update
{

    private CheckH5UpdateInterface mInterface;

    public void setCheckApkUpdateInterface(CheckH5UpdateInterface checkH5UpdateInterface){
        mInterface = checkH5UpdateInterface;
    }
    public void checkApkUpdate(final String urlPath, final List<WebZipItem> webZipItems)
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
                        List<WebZipItem> items = parserH5UpdateJson(result,webZipItems);
                        mInterface.onCheckH5UpdateSuccess(items);

                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        //                       onError("网络连接失败");
                        mInterface.onCheckH5UpdateError();
                    }
                } catch (Exception e)
                {
                    //                   onError("网络连接异常，请检查网络");
                    System.out.println("网络异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

    /**
     * 获取H5升级信息
     *
     * @param updateInfo
     * @return
     */
    private List<WebZipItem> parserH5UpdateJson(String updateInfo,List<WebZipItem> webZipItems)
    {
        List<WebZipItem> items = new ArrayList<>();
        boolean isUpdateH5 = false;
        try
        {
            JSONObject object = new JSONObject(updateInfo);
            JSONArray jsonArray = object.getJSONArray("zips");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                WebZipItem item = new WebZipItem();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                String path = jsonObject.getString("path");
                System.out.println("name=:" + name + "，version=：" + version + "，path=：" + path);
                for (int j = 0; j < webZipItems.size(); j++)
                {
                    if (webZipItems.get(j).getName().equals(name) && !webZipItems.get(j).getVersion().equals(version))
                    {
                        isUpdateH5 = true;
                    }
                }
                item.setIsModule("N");
                item.setIsUpdate(isUpdateH5);
                item.setName(name);
                item.setVersion(version);
                item.setPath(path);
                System.out.println(item.toString());
                items.add(item);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return items;
    }

    interface CheckH5UpdateInterface{
        abstract void onCheckH5UpdateSuccess(List<WebZipItem> items);
        abstract void onCheckH5UpdateError();
    }
}
