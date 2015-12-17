package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
    private Thread mUnZipThread, mCheckUpdateThread, mJumpThread, mCheckH5UpdateThread;

    private List<WebZipItem> webZipItems;

    private String PAGNAME = "";

    private String mApkVersion = "";

    private AlertDialog.Builder mBuilder;

    private String ApkUpdateUrl = "http://192.168.57.1:8080/MyWebAPI/UpdateServlet?APPID=101&version=1.1.0";

    private String H5UpdateUrl = "http://192.168.57.1:8080/MyWebAPI/H5UpdateServlet?APPID=101&version=1.1.0";

    private Activity mContext;

    public InitFramwork(Activity context)
    {
        this.mContext = context;
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try
        {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        PAGNAME = context.getPackageName();
        mApkVersion = pi.versionName;
        mBuilder = new AlertDialog.Builder(context);
    }

    public void init()
    {
        unZipRes();
        checkApkUpdate(ApkUpdateUrl);
    }

    /**
     * 请求APK升级接口，并作升级处理
     *
     * @param urlPath
     */
    private void checkApkUpdate(final String urlPath)
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
                        ApkUpdateItem item = parserApkUpdateJson(result);
                        if ((item != null) && item.isUpdate())
                        {
                            if (item.getType() == 1)
                            {
                                setApkFoceUpdateBuilder();
                            } else
                            {
                                setApkNormalUpdateBuilder();
                            }
                        } else
                        {
                            checkH5Update(H5UpdateUrl);
                        }

                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        onError("网络连接失败");
                    }
                } catch (Exception e)
                {
                    onError("网络连接异常，请检查网络");
                    System.out.println("网络异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        );
        mCheckUpdateThread.start();
    }

    /**
     * 请求H5升级接口，并作升级处理
     *
     * @param urlPath
     */
    private void checkH5Update(final String urlPath)
    {
        mCheckH5UpdateThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    mCheckUpdateThread.join();
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
                        List<WebZipItem> items = parserH5UpdateJson(result);
                        boolean isAlert = false;
                        for (int i = 0; i < items.size(); i++)
                        {
                            WebZipItem tmp = items.get(i);
                            if ((tmp != null) && tmp.isUpdate())
                            {
                                isAlert = true;
                            }
                        }
                        if (isAlert)
                        {
                            setH5NormalUpdateBuilder();
                        } else
                        {
                            setmJumpThread();
                        }
                    } else
                    {
                        System.out.println("------------------链接失败-----------------");
                        onError("网络连接失败");
                    }
                } catch (Exception e)
                {
                    onError("网络连接异常，请检查网络");
                    System.out.println("网络异常：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        );
        mCheckH5UpdateThread.start();
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

    /**
     * 设置APK强制升级时提示框的样式，以及点击事件的处理
     */
    private void setApkFoceUpdateBuilder()
    {
        System.out.println("强制升级处理");
        mBuilder.setTitle("升级提示");
        mBuilder.setMessage("您需要升级才能继续操作。");
        mBuilder.setPositiveButton("确认",
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //TODO下载APK并安装
                    }
                });
        onShowUpdateInfo(mBuilder);
    }

    /**
     * 设置APK一般升级时提示框的样式，以及点击事件的处理
     */
    private void setApkNormalUpdateBuilder()
    {
        System.out.println("一般升级处理");
        mBuilder.setTitle("升级提示");
        mBuilder.setMessage("您需要升级才能继续操作。");
        mBuilder.setPositiveButton("确认",
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //TODO下载H5资源包并解压
                    }
                });
        mBuilder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        checkH5Update(H5UpdateUrl);
                    }
                });
        onShowUpdateInfo(mBuilder);
    }

    /**
     * 设置H5一般升级时提示框的样式，以及点击事件的处理
     */
    private void setH5NormalUpdateBuilder()
    {
        System.out.println("h5升级处理");
        mBuilder.setTitle("升级提示");
        mBuilder.setMessage("您需要下载资源包才能继续操作。");
        mBuilder.setPositiveButton("确认",
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //TODO下载APK并安装
                    }
                });
        mBuilder.setNegativeButton("取消",
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        setmJumpThread();
                    }
                });
        onShowUpdateInfo(mBuilder);
    }

    /**
     * 显示升级提示信息
     *
     * @param builder
     */
    private void onShowUpdateInfo(final AlertDialog.Builder builder)
    {
        mContext.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                builder.show();
            }
        });
    }

    /**
     * 获取APK升级信息
     *
     * @param updateInfo
     * @return
     */
    private ApkUpdateItem parserApkUpdateJson(String updateInfo)
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

    /**
     * 获取H5升级信息
     *
     * @param updateInfo
     * @return
     */
    private List<WebZipItem> parserH5UpdateJson(String updateInfo)
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

    /**
     * 跳转设置
     */
    private void setmJumpThread()
    {
        mJumpThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    mCheckUpdateThread.join();
                    Thread.sleep(1000);
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                    mContext.finish();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        mJumpThread.start();
    }
}
