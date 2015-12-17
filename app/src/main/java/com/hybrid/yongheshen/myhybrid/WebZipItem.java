package com.hybrid.yongheshen.myhybrid;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class WebZipItem
{
    private String name;

    private String version;

    private String path;

    private String isModule;

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

    public String getIsModule()
    {
        return isModule;
    }

    public void setIsModule(String isModule)
    {
        this.isModule = isModule;
    }

    @Override
    public String toString()
    {
        return "WebZipItem{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                ", isModule='" + isModule + '\'' +
                ", isUpdate=" + isUpdate +
                '}';
    }
}
