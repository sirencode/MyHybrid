package com.hybrid.yongheshen.myhybrid.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： shenyonghe689 on 15/12/23.
 */
public class FileUtil
{
    // 获取当前目录下所有的mp4文件
    public static List<String> GetZipFileName(String fileAbsolutePath)
    {
        List<String> vecFile = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        if (subFile == null)
        {
            return null;
        }

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++)
        {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory())
            {
                String filename = subFile[iFileLength].getName();
                // 判断是否为MP4结尾
                if (filename.trim().toLowerCase().endsWith(".zip"))
                {
                    vecFile.add(filename);
                }
            }
        }
        return vecFile;
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    FileUtil.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            System.out.println("文件不存在！");
        }
    }
}
