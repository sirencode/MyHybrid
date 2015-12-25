package com.hybrid.yongheshen.myhybrid.util;

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

import com.hybrid.yongheshen.myhybrid.MainActivity;
import com.hybrid.yongheshen.myhybrid.R;
import com.hybrid.yongheshen.myhybrid.update.AutoInstall;
import com.hybrid.yongheshen.myhybrid.update.CheckApkUpdate;
import com.hybrid.yongheshen.myhybrid.update.CheckH5Update;
import com.hybrid.yongheshen.myhybrid.update.ParserConfig;
import com.hybrid.yongheshen.myhybrid.update.WebZipItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： shenyonghe689 on 15/12/11.
 */
public class InitFramwork implements CheckH5Update.CheckH5UpdateInterface
{
    private Thread mUnZipThread, mJumpThread;

    private List<WebZipItem> mUpdateWebZips;

    private String PAGNAME = "";

    private String mApkDownloadPath;

    private int TYPE_APK = 1, TYPE_H5 = 2;

    private boolean mIsDownload;

    private Activity mContext;

    private AlertDialog.Builder mBuilder;

    private TextView tv_Title;

    private Button btnSure, btnCancle;

    private ProgressBar progressBar;

    private CheckH5Update mCheckH5Update = new CheckH5Update();

    private DownloadFileUtil mDownloadFileUtil;

    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MyHybridConfig.APK_FOCE_SHOW:
                    onShowUpdateInfo(TYPE_APK, true);
                    break;

                case MyHybridConfig.APK_NORMAL_SHOW:
                    onShowUpdateInfo(TYPE_APK, false);
                    break;

                case MyHybridConfig.H5_FOCE_SHOW:
                    onShowUpdateInfo(TYPE_H5, true);
                    break;

                case MyHybridConfig.H5_NORMAL_SHOW:
                    onShowUpdateInfo(TYPE_H5, false);
                    break;

                case MyHybridConfig.ALERT_H5ERROR:
                    ToastUtil.showToast(mContext, "H5资源包找不到了！");
                    break;

                case MyHybridConfig.ALERT_NETERROR:
                    ToastUtil.showToast(mContext, "网络异常！");
                    break;

                case MyHybridConfig.ALERT_APKUPDATEERROR:
                    ToastUtil.showToast(mContext, "应用更新失败，请重试！");
                    break;

                case MyHybridConfig.ALERT_DONEDOWNLOADAPK:
                    ToastUtil.showToast(mContext, "更新完毕！");
                    tv_Title.setText("下载完成");
                    btnSure.setText("安装");
                    mIsDownload = true;
                    btnSure.setClickable(true);
                    break;

                case MyHybridConfig.UPDATEPROCESSBAR:
                    progressBar.setProgress(msg.arg1);
                    break;

                case MyHybridConfig.ALERT_DONEDOWNLOADH5:
                    mUpdateWebZips = null;
                    final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage
                            (mContext.getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
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
        MyHybridConfig.ApkVersion = pi.versionName;
        mBuilder = new AlertDialog.Builder(context);
        MyHybridConfig.ZipPath = "data/data/" + PAGNAME + "/webroot/download/";
        mApkDownloadPath = Environment.getExternalStorageDirectory() +
                "/DownLoad/";
    }

    public void init()
    {
        MyHybridConfig.WebZipItems = new ArrayList<>();
        //解析配置文件config.xml
        MyHybridConfig.WebZipItems = ParserConfig.getWebZipItem(mContext);

        if (MyHybridConfig.Evnvionment == null)
        {
            ToastUtil.showToast(mContext, "配置文件出错");
        } else
        {
            unZipRes();
            if (MyHybridConfig.Evnvionment.equals("dev"))
            {
                setmJumpThread();
            } else
            {
                CheckApkUpdate mCheckApkUpdate = new CheckApkUpdate(mHandler, mCheckH5Update);
                mCheckH5Update.setCheckApkUpdateInterface(this);
                if (MyHybridConfig.ApkUpdateUrl == null || MyHybridConfig.ApkUpdateUrl.equals(""))
                {
                    ToastUtil.showToast(mContext, "找不到APK的更新地址");
                } else
                {
                    mCheckApkUpdate.checkApkUpdate(MyHybridConfig.ApkUpdateUrl, MyHybridConfig.ApkVersion);
                }
            }
        }

    }

    /**
     * 将资源包解压到data/data/<包名> 的根目录下
     * <p/>
     * 获取到配置文件中声明的所有的离线zip资源包
     */
    private void unZipRes()
    {

        mUnZipThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                MyHybridConfig.ZipList = FileUtil.GetZipFileName(MyHybridConfig.ZipPath);
                if (MyHybridConfig.ZipList == null)
                {
                    AssetCopyer assetCopyer = new AssetCopyer(mContext);
                    assetCopyer.copy();
                    System.out.println("第一次加载");
                }

                MyHybridConfig.ZipList = FileUtil.GetZipFileName(MyHybridConfig.ZipPath);

                for (int i = 0; i < MyHybridConfig.ZipList.size(); i++)
                {
                    System.out.println("文件夹下的压缩包" + MyHybridConfig.ZipList.get(i));
                    File file = new File(MyHybridConfig.ZipPath + MyHybridConfig.ZipList.get(i));
                    InputStream inputStream = fileToInputStream(file);
                    System.out.println(FileToMD5.md5sum(inputStream));
                }

                for (int i = 0; i < MyHybridConfig.ZipList.size(); i++)
                {
                    File file = new File(MyHybridConfig.ZipPath + MyHybridConfig.ZipList.get(i));
                    InputStream inputStream = fileToInputStream(file);
                    String desPath = "data/data/" + PAGNAME + "/webroot/";
                    UnZipUtil.Unzip(inputStream, desPath);
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
                mDownloadFileUtil = new DownloadFileUtil(mHandler);
                if (type == TYPE_APK)
                {
                    if (!mIsDownload)
                    {
                        downloadApk(MyHybridConfig.ApkUpdateItem.getAppDownLoadUrl(), MyHybridConfig.ApkUpdateItem.getAppName());
                    } else
                    {
                        AutoInstall.setUrl(mApkDownloadPath + MyHybridConfig.ApkUpdateItem.getAppName());
                        AutoInstall.install(mContext);
                    }
                } else
                {
                    ToastUtil.showToast(mContext, "下载H5");
                    String name = "core.zip";
                    //创建文件夹 DownLoad，在存储卡下
                    WebZipItem item = mUpdateWebZips.get(0);
                    mDownloadFileUtil.downloadFiles(progressBar, mUpdateWebZips, MyHybridConfig.ZipPath);
                }
                v.setClickable(false);
            }
        });
        btnCancle = (Button) layout.findViewById(R.id.btn_updateCancle);
        if (type == TYPE_APK)
        {
            tv_Title.setText(MyHybridConfig.ApkUpdateItem.getUpdateMsg());
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
                v.setClickable(false);
                if (type == TYPE_APK)
                {
                    if (MyHybridConfig.H5UpdateUrl == null || MyHybridConfig.H5UpdateUrl.equals(""))
                    {
                        ToastUtil.showToast(mContext, "找不到资源包得更新地址");
                    } else
                    {
                        mCheckH5Update.checkApkUpdate(MyHybridConfig.H5UpdateUrl, MyHybridConfig.WebZipItems);
                    }
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

        mUpdateWebZips = new ArrayList<>();

        for (int i = 0; i < items.size(); i++)
        {
            WebZipItem tmp = items.get(i);
            if (tmp.isNeedUpdate())
            {
                System.out.println("<======updata========>" + tmp.getModuleName() + tmp
                        .getModuleMd5());
                mUpdateWebZips.add(tmp);
            }
        }
        if (mUpdateWebZips.size() > 0)
        {
            mHandler.sendEmptyMessage(MyHybridConfig.H5_FOCE_SHOW);
        } else
        {
            setmJumpThread();
        }
    }

    @Override
    public void onCheckH5UpdateError()
    {
        mHandler.sendEmptyMessage(MyHybridConfig.ALERT_NETERROR);
    }

    private void downloadApk(String downloadUrl, String name)
    {
        ToastUtil.showToast(mContext, "下载APK");
        //创建文件夹 MyDownLoad，在存储卡下
        mDownloadFileUtil.downloadFile(progressBar, downloadUrl, name,
                mApkDownloadPath);
    }

    public static InputStream fileToInputStream(File file)
    {
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return inputStream;
    }

}


