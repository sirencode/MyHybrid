package com.hybrid.yongheshen.myhybrid;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author shenyonghe
 *         <p/>
 *         2015-12-8
 */
public class UnZipUtil
{
    /**
     * @param inputStream 解压文件的流（AS assets 目录问题）
     * @param targetDir   解压的路径
     */
    public static void Unzip(InputStream inputStream, String targetDir)
    {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称

        try
        {
            BufferedOutputStream dest = null; //缓冲输出流
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry entry = null; //每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null)
            {

                try
                {
                    Log.i("Unzip: ", "=" + entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();
                    System.out.println(strEntry);
                    if (!strEntry.contains("."))
                    {
                        continue;
                    }
                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists())
                    {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1)
                    {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            }
            zis.close();
        } catch (Exception cwj)
        {
            cwj.printStackTrace();
            System.out.println(cwj.getMessage());
        }
    }
}
