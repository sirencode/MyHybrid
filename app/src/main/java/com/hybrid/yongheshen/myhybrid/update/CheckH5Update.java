package com.hybrid.yongheshen.myhybrid.update;

import com.hybrid.yongheshen.myhybrid.util.FileToMD5;
import com.hybrid.yongheshen.myhybrid.util.InitFramwork;
import com.hybrid.yongheshen.myhybrid.util.MyHybridConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： shenyonghe689 on 15/12/17.
 */
public class CheckH5Update
{

    private CheckH5UpdateInterface mInterface;

    public void setCheckApkUpdateInterface(CheckH5UpdateInterface checkH5UpdateInterface)
    {
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
                                    "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 " +
                                            "Firefox/27.0");
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
                        List<WebZipItem> items = parserH5UpdateJson(result, webZipItems);
                        mInterface.onCheckH5UpdateSuccess(items);

                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        //                       onError("网络连接失败");
                        mInterface.onCheckH5UpdateError();
                    }
                } catch (Exception e)
                {
                    mInterface.onCheckH5UpdateError();
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
    private List<WebZipItem> parserH5UpdateJson(String updateInfo, List<WebZipItem> webZipItems)
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
                String moduleName = jsonObject.getString("moduleName");
                String updateUrl = jsonObject.getString("updateUrl");
                String moduleMd5 = jsonObject.getString("moduleMd5");
                System.out.println("name=:" + moduleName + "，md5=：" + moduleMd5);
                for (int j = 0; j < MyHybridConfig.ZipList.size(); j++)
                {
                    File file = new File(MyHybridConfig.ZipPath + MyHybridConfig.ZipList.get(j));
                    InputStream inputStream = InitFramwork.fileToInputStream(file);
                    String zipMd5 = FileToMD5.md5sum(inputStream);
                    System.out.println(zipMd5);
                    if (MyHybridConfig.ZipList.contains(moduleName) && zipMd5.equals(moduleMd5))
                    {
                        isUpdateH5 = false;
                        break;
                    } else
                    {
                        isUpdateH5 = true;
                    }
                }
                item.setIsNeedUpdate(isUpdateH5);
                item.setModuleName(moduleName);
                item.setUpdateUrl(updateUrl);
                item.setModuleMd5(moduleMd5);
                System.out.println(item.getModuleName()+item.getModuleMd5());
                items.add(item);
            }

        } catch (JSONException e)
        {

            mInterface.onCheckH5UpdateError();
            System.out.println("h5更新接口返回参数问题：" + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    public interface CheckH5UpdateInterface
    {
        abstract void onCheckH5UpdateSuccess(List<WebZipItem> items);
        abstract void onCheckH5UpdateError();
    }
}
