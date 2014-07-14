package com.larry.alipaycracker;

import java.security.MessageDigest;

public class SHA1
{
  public static String byte2hex(byte[] paramArrayOfByte)
  {
    String str1 = "";
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
      if (str2.length() == 1);
      for (str1 = str1 + "0" + str2; ; str1 = str1 + str2)
      {
        i++;
        break;
      }
    }
    return str1;
  }

  public static final String byte2hexString(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      if ((0xFF & paramArrayOfByte[i]) < 16)
        localStringBuffer.append("0");
      localStringBuffer.append(Long.toString(0xFF & paramArrayOfByte[i], 16));
    }
    return localStringBuffer.toString();
  }

  public static String sha1(String paramString)
  {
    try
    {
      String str = byte2hexString(MessageDigest.getInstance("SHA-1").digest(paramString.getBytes()));
      return str;
    }
    catch (Exception localException)
    {
    }
    return paramString;
  }
}