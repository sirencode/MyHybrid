package com.hybrid.yongheshen.myhybrid;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 作者： yongheshen on 15/12/10.
 */
public class FileToMD5
{
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHexString(byte[] b)
    {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++)
        {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5sum(InputStream inputStream)
    {
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = inputStream.read(buffer)) > 0)
            {
                md5.update(buffer, 0, numRead);
            }
            inputStream.close();
            return toHexString(md5.digest());
        } catch (Exception e)
        {
            System.out.println("error");
            return null;
        }
    }
}
