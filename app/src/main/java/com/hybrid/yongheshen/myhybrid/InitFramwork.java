package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
public class InitFramwork implements CheckH5Update.CheckH5UpdateInterface
{
    private Thread mUnZipThread, mJumpThread;

    public static List<WebZipItem> webZipItems;

    private String PAGNAME = "";

    private String mApkVersion = "";

    private AlertDialog.Builder mBuilder;

    private String ApkUpdateUrl = "http://192.168.57.1:8080/MyWebAPI/UpdateServlet?APPID=101&version=1.1.0";

    private String H5UpdateUrl = "http://192.168.57.1:8080/MyWebAPI/H5UpdateServlet?APPID=101&version=1.1.0";

    private int TYPE_APK = 1, TYPE_H5 = 2;

    private Activity mContext;

    private TextView tv_Title;

    private Button btnSure, btnCancle;

    private ProgressBar progressBar;

    public static final int APK_FOCE_SHOW = 0x0001, APK_NORMAL_SHOW = 0x0002, H5_FOCE_SHOW = 0x0003, H5_NORMAL_SHOW = 0x0004;

    public static final int ALERT_H5ERROR = 0x0005, ALERT_NETERROR = 0x0006, ALERT_APKUPDATEERROR = 0x0007,
            ALERT_DONEDOWNLOADAPK = 0x0008, UPDATEPROCESSBAR = 0x0009;

    private CheckH5Update mCheckH5Update = new CheckH5Update();

    private DownloadFileUtil mDownloadFileUtil;

    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case APK_FOCE_SHOW:
                    onShowUpdateInfo(TYPE_APK, true);
                    break;

                case APK_NORMAL_SHOW:
                    onShowUpdateInfo(TYPE_APK, false);
                    break;

                case H5_FOCE_SHOW:
                    onShowUpdateInfo(TYPE_H5, true);
                    break;

                case H5_NORMAL_SHOW:
                    onShowUpdateInfo(TYPE_H5, false);
                    break;

                case ALERT_H5ERROR:
                    ToastUtil.showToast(mContext, "H5资源包找不到了！");
                    break;

                case ALERT_NETERROR:
                    ToastUtil.showToast(mContext, "网络异常！");
                    break;

                case ALERT_APKUPDATEERROR:
                    ToastUtil.showToast(mContext, "应用更新失败，请重试！");
                    break;

                case ALERT_DONEDOWNLOADAPK:
                    ToastUtil.showToast(mContext, "更新完毕！");
                    tv_Title.setText("下载完成");
                    btnSure.setText("安装");
                    break;

                case UPDATEPROCESSBAR:
                    progressBar.setProgress(msg.arg1);
                    break;

                default:
                    break;
            }
        }
    };

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
        CheckApkUpdate mCheckApkUpdate = new CheckApkUpdate(mHandler, mCheckH5Update);
        mCheckH5Update.setCheckApkUpdateInterface(this);
        mDownloadFileUtil = new DownloadFileUtil(mHandler);
        mCheckApkUpdate.checkApkUpdate(ApkUpdateUrl, mApkVersion);
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
                        mHandler.sendEmptyMessage(ALERT_H5ERROR);
                    }
                }
            }
        });
        mUnZipThread.start();
    }

    /**
     * 显示升级提示信息
     */
    private void onShowUpdateInfo(final int type, final boolean isFoced)
    {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View layout = inflater.inflate(R.layout.update, null);
        tv_Title = (TextView) layout.findViewById(R.id.tv_update);
        progressBar = (ProgressBar) layout.findViewById(R.id.pb_update);
        progressBar.setVisibility(View.INVISIBLE);
        btnSure = (Button) layout.findViewById(R.id.btn_updateSure);
        btnSure.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                tv_Title.setText("开始下载，请稍后！");
                v.setClickable(false);
                if (type == TYPE_APK)
                {
                    ToastUtil.showToast(mContext, "下载APK");
                    new Thread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            String downloadUrl = "http://img5.duitang.com/uploads/item/201408/16/20140816224113_wM3nH.png";
                            String name = "MeiNv.png";
                            //创建文件夹 MyDownLoad，在存储卡下
                            String dirName = Environment.getExternalStorageDirectory() + "/MyDownLoad/";
                            mDownloadFileUtil.downloaKAPk(progressBar, downloadUrl, name, dirName);
                        }
                    }).start();
                } else
                {
                    ToastUtil.showToast(mContext, "下载H5");
                }
            }
        });
        btnCancle = (Button) layout.findViewById(R.id.btn_updateCancle);
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
    public void onCheckH5UpdateSuccess(List<WebZipItem> items)
    {
        boolean isAlert = false;
        if (items.size() != webZipItems.size())
        {
            isAlert = true;
        }
        for (int i = 0; i < items.size(); i++)
        {
            WebZipItem tmp = items.get(i);
            if (((tmp != null) && tmp.isUpdate()))
            {
                isAlert = true;
            }
        }
        if (isAlert)
        {
            mHandler.sendEmptyMessage(H5_NORMAL_SHOW);
        } else
        {
            setmJumpThread();
        }
    }

    @Override
    public void onCheckH5UpdateError()
    {
        mHandler.sendEmptyMessage(ALERT_NETERROR);
    }
}
