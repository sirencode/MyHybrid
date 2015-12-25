package com.hybrid.yongheshen.myhybrid;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class XmlPullParserUtils
{
    public static List<WebZipItem> getWebZipItem(Context context)
    {
        //定义事件类型
        int eventType = 0;
        WebZipItem currentZipItem = null;
        List<WebZipItem> zipItems = new ArrayList<WebZipItem>();
        try
        {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.config);
            // 获取到xml文件时，XmlResourceParser的是指向文档开始处
            eventType = xrp.getEventType();
            // System.out.println("-->"+eventType);//查看事件的数值
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        System.out.println("Start document");
                        break;
                    case XmlPullParser.START_TAG:
                        String tagName = xrp.getName();
                        if ((tagName != null) && tagName.equals("item"))
                        {
                            currentZipItem = new WebZipItem();
                        } else if ((tagName != null) && tagName.equals("moduleName"))
                        {
                            String moduleName = xrp.getAttributeValue(null, "value");
                            currentZipItem.setModuleName(moduleName);
                        } else if ((tagName != null) && tagName.equals("updateUrl"))
                        {
                            String updateUrl = xrp.getAttributeValue(null, "value");
                            currentZipItem.setUpdateUrl(updateUrl);
                        } else if ((tagName != null) && tagName.equals("moduleMd5"))
                        {
                            String moduleMd5 = xrp.getAttributeValue(null, "value");
                            currentZipItem.setModuleMd5(moduleMd5);
                        }
                        else if ((tagName != null) && tagName.equals("url_apk_check_upgrade"))
                        {
                            String apkUpdateUrl = xrp.getAttributeValue(null, "value");
                            InitFramwork.ApkUpdateUrl = apkUpdateUrl+InitFramwork.mApkUrlParams;
                        }
                        else if ((tagName != null) && tagName.equals("url_module_check_upgrade"))
                        {
                            String h5UpdateUrl = xrp.getAttributeValue(null, "value");
                            InitFramwork.H5UpdateUrl = h5UpdateUrl+InitFramwork.mH5UrlParams;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        System.out.println("Text " + xrp.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        String endTagName = xrp.getName();
                        if ((endTagName != null) && endTagName.equals("item"))
                        {
                            zipItems.add(currentZipItem);
                            currentZipItem = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = xrp.next();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //判断事件类型是不是文档结束
        if (eventType == XmlPullParser.END_DOCUMENT)
        {
            System.out.println("End document");
        }

        for (int i = 0; i < zipItems.size(); i++)
        {
            System.out.println("Items info ====");
            System.out.println("name = " + zipItems.get(i).getModuleName());
            System.out.println("path = " + zipItems.get(i).getUpdateUrl());
            System.out.println("version = " + zipItems.get(i).getModuleMd5());
        }

        return zipItems;
    }
}
