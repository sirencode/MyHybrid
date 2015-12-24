package com.hybrid.yongheshen.myhybrid;

/**
 * 作者： yongheshen on 15/12/16.
 * APK升级信息包装处理
 */
public class ApkUpdateItem
{
    private String appDownLoadUrl;

    private String lastAppVersion;

    private String updateMsg;

    private boolean isUpdate;

    private String updateFlag;

    private String appSize;

    private String appName;

    public String getAppDownLoadUrl()
    {
        return appDownLoadUrl;
    }

    public void setAppDownLoadUrl(String appDownLoadUrl)
    {
        this.appDownLoadUrl = appDownLoadUrl;
    }

    public String getLastAppVersion()
    {
        return lastAppVersion;
    }

    public void setLastAppVersion(String lastAppVersion)
    {
        this.lastAppVersion = lastAppVersion;
    }

    public String getUpdateMsg()
    {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg)
    {
        this.updateMsg = updateMsg;
    }

    public boolean isUpdate()
    {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getUpdateFlag()
    {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag)
    {
        this.updateFlag = updateFlag;
    }

    public String getAppSize()
    {
        return appSize;
    }

    public void setAppSize(String appSize)
    {
        this.appSize = appSize;
    }

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    @Override
    public String toString()
    {
        return "ApkUpdateItem{" +
                "appDownLoadUrl='" + appDownLoadUrl + '\'' +
                ", lastAppVersion='" + lastAppVersion + '\'' +
                ", updateMsg='" + updateMsg + '\'' +
                ", isUpdate=" + isUpdate +
                ", updateFlag='" + updateFlag + '\'' +
                ", appSize='" + appSize + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
