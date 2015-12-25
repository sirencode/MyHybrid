package com.hybrid.yongheshen.myhybrid.util;

import com.hybrid.yongheshen.myhybrid.update.WebZipItem;

import java.util.List;

/**
 * 作者： shenyonghe689 on 15/12/25.
 */
public class MyHybridConfig
{
    //压缩包资源所在的路径
    public static String ZipPath;

    //获取更新相关信息
    public static com.hybrid.yongheshen.myhybrid.update.ApkUpdateItem ApkUpdateItem;

    //检查更新APK的路径，加了参数
    public static String ApkUpdateUrl;

    ////检查更新h5的路径，加了参数
    public static String H5UpdateUrl;

    //检查APK更新的参数
    public static String APPID;

    //配置文件里面的环境设置，开发测试生产
    public static String Evnvionment;

    //读取配置文件里面的资源包列表
    public static List<WebZipItem> WebZipItems;

    public static String ApkVersion;

    //扫描资源包目录下的所有zip文件
    public static List<String> ZipList;

    public static final int APK_FOCE_SHOW = 0x0001, APK_NORMAL_SHOW = 0x0002, H5_FOCE_SHOW =
            0x0003, H5_NORMAL_SHOW = 0x0004;

    public static final int ALERT_H5ERROR = 0x0005, ALERT_NETERROR = 0x0006, ALERT_APKUPDATEERROR
            = 0x0007, ALERT_DONEDOWNLOADAPK = 0x0008, UPDATEPROCESSBAR = 0x0009,
            ALERT_DONEDOWNLOADH5 = 0x0010;
}
