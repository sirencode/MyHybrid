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
                        } else if ((tagName != null) && tagName.equals("name"))
                        {
                            String name = xrp.getAttributeValue(null, "value");
                            currentZipItem.setName(name);
                        } else if ((tagName != null) && tagName.equals("version"))
                        {
                            float version = Float.parseFloat(xrp.getAttributeValue(null, "value"));
                            currentZipItem.setVersion(version);
                        } else if ((tagName != null) && tagName.equals("path"))
                        {
                            String path = xrp.getAttributeValue(null, "value");
                            currentZipItem.setPath(path);
                        } else if ((tagName != null) && tagName.equals("isModule"))
                        {
                            String isModule = xrp.getAttributeValue(null, "value");
                            currentZipItem.setIsModule(isModule);
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
            System.out.println("name = " + zipItems.get(i).getName());
            System.out.println("path = " + zipItems.get(i).getPath());
            System.out.println("isM = " + zipItems.get(i).getIsModule());
            System.out.println("version = " + zipItems.get(i).getVersion());
        }

        return zipItems;
    }
}
