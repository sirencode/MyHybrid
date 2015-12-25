package com.hybrid.yongheshen.myhybrid.update;

import java.io.Serializable;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class WebZipItem implements Serializable
{
    private String moduleName;

    private String updateUrl;

    private String moduleMd5;

    private boolean isNeedUpdate;

    public boolean isNeedUpdate()
    {
        return isNeedUpdate;
    }

    public void setIsNeedUpdate(boolean isNeedUpdate)
    {
        this.isNeedUpdate = isNeedUpdate;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getUpdateUrl()
    {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl)
    {
        this.updateUrl = updateUrl;
    }

    public String getModuleMd5()
    {
        return moduleMd5;
    }

    public void setModuleMd5(String moduleMd5)
    {
        this.moduleMd5 = moduleMd5;
    }

    @Override
    public String toString()
    {
        return "WebZipItem{" +
                "moduleName='" + moduleName + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", moduleMd5='" + moduleMd5 + '\'' +
                ", isNeedUpdate=" + isNeedUpdate +
                '}';
    }
}
