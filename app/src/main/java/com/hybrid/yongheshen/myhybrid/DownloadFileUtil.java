package com.hybrid.yongheshen.myhybrid;

import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 作者： shenyonghe689 on 15/12/18.
 */
public class DownloadFileUtil
{
    private Handler mHandler;
    private boolean  mIsApk;

    public DownloadFileUtil(Handler handler,boolean apk){
        this.mHandler = handler;
        this.mIsApk = apk;
    }

    //下载具体操作
    public void downloadFile(final ProgressBar progressBar, final String downloadUrl, final
    String name, final String dirName)
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    URL url = new URL(downloadUrl);
                    //打开连接
                    URLConnection conn = url.openConnection();
                    //打开输入流
                    InputStream is = conn.getInputStream();
                    //获得长度
                    int contentLength = conn.getContentLength();
                    progressBar.setMax(contentLength);
                    System.out.println("contentLength = " + contentLength);
                    File file = new File(dirName);
                    //不存在创建
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    //下载后的文件名
                    String fileName = dirName + name;
                    File file1 = new File(fileName);
                    if (file1.exists())
                    {
                        file1.delete();
                    }
                    //创建字节流
                    byte[] bs = new byte[1024];
                    int tmp = 0;
                    int len;
                    OutputStream os = new FileOutputStream(fileName);
                    //写数据
                    while ((len = is.read(bs)) != -1)
                    {
                        os.write(bs, 0, len);
                        tmp += len;
                        final int finalTmp = tmp;
                        setDownloadProgress(progressBar, finalTmp);
                    }
                    //完成后关闭流
                    System.out.println("download-finish");
                    os.close();
                    is.close();
                    if (mIsApk)
                    {
                        onDownloadApkFinish();
                    }
                    else {
                        onDownloadH5Finish();
                    }
                } catch (Exception e)
                {
                    downloadErro();
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void setDownloadProgress(ProgressBar pb, int progress)
    {
        Message message = Message.obtain();
        message.what = InitFramwork.UPDATEPROCESSBAR;
        message.arg1 = progress;
        mHandler.sendMessage(message);
    }

    private void downloadErro()
    {
        mHandler.sendEmptyMessage(InitFramwork.ALERT_DONEDOWNLOADAPK);
    }

    /**
     * APK下载完成
     */
    private void onDownloadApkFinish()
    {
        mHandler.sendEmptyMessage(InitFramwork.ALERT_DONEDOWNLOADAPK);
    }

    /**
     * H5下载完成
     */
    private void onDownloadH5Finish()
    {
        mHandler.sendEmptyMessage(InitFramwork.ALERT_DONEDOWNLOADH5);
    }

}
