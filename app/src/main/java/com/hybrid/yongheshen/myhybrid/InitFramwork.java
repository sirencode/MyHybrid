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

    public static List<WebZipItem> webZipItems;

    private List<WebZipItem> mUpdateWebZips;

    public static List<String> mZipList;

    private String PAGNAME = "";

    private String mApkVersion = "";

    public static String mZipPath;

    private AlertDialog.Builder mBuilder;

    public static ApkUpdateItem apkUpdateItem;

    public static String ApkUpdateUrl;

    public static String H5UpdateUrl;

    private String mApkUrlParams = "?appID=101&platform=Android";

    private String mH5UrlParams;

    private String mApkDownloadPath;

    private int TYPE_APK = 1, TYPE_H5 = 2;

    private boolean mIsDownload;

    private Activity mContext;

    private TextView tv_Title;

    private Button btnSure, btnCancle;

    private ProgressBar progressBar;

    public static final int APK_FOCE_SHOW = 0x0001, APK_NORMAL_SHOW = 0x0002, H5_FOCE_SHOW =
            0x0003, H5_NORMAL_SHOW = 0x0004;

    public static final int ALERT_H5ERROR = 0x0005, ALERT_NETERROR = 0x0006, ALERT_APKUPDATEERROR
            = 0x0007, ALERT_DONEDOWNLOADAPK = 0x0008, UPDATEPROCESSBAR = 0x0009,
            ALERT_DONEDOWNLOADH5 = 0x0010;

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
                    mIsDownload = true;
                    btnSure.setClickable(true);
                    break;

                case UPDATEPROCESSBAR:
                    progressBar.setProgress(msg.arg1);
                    break;

                case ALERT_DONEDOWNLOADH5:
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
        mApkVersion = pi.versionName;
        mBuilder = new AlertDialog.Builder(context);
        mZipPath = "data/data/" + PAGNAME + "/webroot/download/";
        mApkDownloadPath = Environment.getExternalStorageDirectory() +
                "/DownLoad/";
        mH5UrlParams = "?appID=101&appVersion="+mApkVersion+"&platform=Android";
    }

    public void init()
    {
        unZipRes();
        CheckApkUpdate mCheckApkUpdate = new CheckApkUpdate(mHandler, mCheckH5Update);
        mCheckH5Update.setCheckApkUpdateInterface(this);
        if (ApkUpdateUrl == null || ApkUpdateUrl.equals(""))
        {
            ToastUtil.showToast(mContext, "找不到APK的更新地址");
        } else
        {
            mCheckApkUpdate.checkApkUpdate(ApkUpdateUrl + mApkUrlParams, mApkVersion);
        }

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
                mZipList = FileUtil.GetZipFileName(mZipPath);
                if (mZipList == null)
                {
                    AssetCopyer assetCopyer = new AssetCopyer(mContext);
                    assetCopyer.copy();
                    System.out.println("第一次加载");
                }

                mZipList = FileUtil.GetZipFileName(mZipPath);

                for (int i = 0; i < mZipList.size(); i++)
                {
                    System.out.println("文件夹下的压缩包" + mZipList.get(i));
                    File file = new File(mZipPath + mZipList.get(i));
                    InputStream inputStream = fileToInputStream(file);
                    System.out.println(FileToMD5.md5sum(inputStream));
                }

                for (int i = 0; i < mZipList.size(); i++)
                {
                    File file = new File(mZipPath + mZipList.get(i));
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
                        downloadApk(apkUpdateItem.getAppDownLoadUrl(), apkUpdateItem.getAppName());
                    } else
                    {
                        AutoInstall.setUrl(mApkDownloadPath + apkUpdateItem.getAppName());
                        AutoInstall.install(mContext);
                    }
                } else
                {
                    ToastUtil.showToast(mContext, "下载H5");
                    String name = "core.zip";
                    //创建文件夹 DownLoad，在存储卡下
                    WebZipItem item = mUpdateWebZips.get(0);
                    mDownloadFileUtil.downloadFiles(progressBar, mUpdateWebZips, mZipPath);
                }
                v.setClickable(false);
            }
        });
        btnCancle = (Button) layout.findViewById(R.id.btn_updateCancle);
        if (type == TYPE_APK)
        {
            tv_Title.setText(apkUpdateItem.getUpdateMsg());
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
                    if (H5UpdateUrl == null || H5UpdateUrl.equals(""))
                    {
                        ToastUtil.showToast(mContext,"找不到资源包得更新地址");
                    }
                    else
                    {
                        mCheckH5Update.checkApkUpdate(H5UpdateUrl+ mH5UrlParams, webZipItems);
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
                System.out.println("<======updata========>" + tmp.getModuleName()+tmp.getModuleMd5());
                mUpdateWebZips.add(tmp);
            }
        }
        if (mUpdateWebZips.size() > 0)
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


