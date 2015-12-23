package com.hybrid.yongheshen.myhybrid;

/**
 * 作者： yongheshen on 15/12/16.
 * APK升级信息包装处理
 */
public class ApkUpdateItem
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String version;

    private int type;

    private boolean isUpdate;

    private String path;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isUpdate()
    {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ApkUpdateItem{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", isUpdate=" + isUpdate +
                ", path='" + path + '\'' +
                '}';
    }
}
