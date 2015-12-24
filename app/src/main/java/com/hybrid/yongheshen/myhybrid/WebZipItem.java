package com.hybrid.yongheshen.myhybrid;

import java.io.Serializable;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class WebZipItem implements Serializable
{
    private String name;

    private String version;

    private String path;

    private String md5;

    public String getMd5()
    {
        return md5;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    private boolean isUpdate;

    public boolean isUpdate()
    {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public String toString()
    {
        return "WebZipItem{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                ", isUpdate=" + isUpdate +
                '}';
    }
}
