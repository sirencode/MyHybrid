package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/11.
 */
public class InitFramwork implements ChechApkUpdate.CheckApkUpdateInterface, CheckH5Update.CheckH5UpdateInterface
{
    private Thread mUnZipThread, mJumpThread;

    private List<WebZipItem> webZipItems;

    private String PAGNAME = "";

    private String mApkVersion = "";

    private AlertDialog.Builder mBuilder;

    private String ApkUpdateUrl = "http://192.168.57.1:8080/MyWebAPI/UpdateServlet?APPID=101&version=1.1.0";

    private String H5UpdateUrl = "http://192.168.57.1:8080/MyWebAPI/H5UpdateServlet?APPID=101&version=1.1.0";

    private int TYPE_APK = 1;

    private int TYPE_H5 = 2;

    private Activity mContext;

    private CheckH5Update mCheckH5Update = new CheckH5Update();

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
        ChechApkUpdate chechApkUpdate = new ChechApkUpdate();
        chechApkUpdate.setCheckApkUpdateInterface(this);
        mCheckH5Update.setCheckApkUpdateInterface(this);
        chechApkUpdate.checkApkUpdate(ApkUpdateUrl, mApkVersion);
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
                        String desPath = "data/data/" + PAGNAME + "/webroot/";
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
     * 显示升级提示信息
     */
    private void onShowUpdateInfo(final int type, final boolean isFoced)
    {
        mContext.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                LayoutInflater inflater = mContext.getLayoutInflater();
                View layout = inflater.inflate(R.layout.update, null);
                TextView tv_Title = (TextView) layout.findViewById(R.id.tv_update);
                final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.pb_update);
                progressBar.setVisibility(View.INVISIBLE);
                Button btnSure = (Button) layout.findViewById(R.id.btn_updateSure);
                btnSure.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        if (type == TYPE_APK)
                        {
                            ToastUtil.showToast(mContext,"下载APK");
                        } else
                        {
                            ToastUtil.showToast(mContext, "下载H5");                        }
                    }
                });
                Button btnCancle = (Button) layout.findViewById(R.id.btn_updateCancle);
                if (type == TYPE_APK)
                {
                    tv_Title.setText("您的APK需要升级,是否现在升级");
                } else
                {
                    tv_Title.setText("您的资源包需要升级,是否现在升级");
                }
                if (isFoced)
                {
                    btnCancle.setVisibility(View.GONE);
                }
                btnCancle.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        if (type == TYPE_APK)
                        {
                            mCheckH5Update.checkApkUpdate(H5UpdateUrl, webZipItems);
                        } else
                        {
                            setmJumpThread();
                        }
                    }
                });
                mBuilder.setView(layout);
                mBuilder.create();
                mBuilder.show();
            }
        });
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

    @Override
    public void onCheckApkUpdateSuccess(ApkUpdateItem item)
    {
        if ((item != null) && item.isUpdate())
        {
            if (item.getType() == 1)
            {
                onShowUpdateInfo(TYPE_APK, true);
            } else
            {
                onShowUpdateInfo(TYPE_APK, false);
            }
        } else
        {
            mCheckH5Update.checkApkUpdate(H5UpdateUrl, webZipItems);
        }
    }

    @Override
    public void onCheckApkUpdateError()
    {
        onError("网络连接失败");
    }

    @Override
    public void onCheckH5UpdateSuccess(List<WebZipItem> items)
    {
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
            onShowUpdateInfo(TYPE_H5, false);
        } else
        {
            setmJumpThread();
        }
    }

    @Override
    public void onCheckH5UpdateError()
    {
        onError("网络连接失败");
    }
}
