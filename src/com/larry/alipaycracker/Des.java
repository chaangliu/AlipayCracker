package com.larry.alipaycracker;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class Des
{
  public static String decrypt(String paramString1, String paramString2)
  {
    if (paramString1.equals(""))
      return "";
    return doFinal(2, paramString1, paramString2);
  }

//  public static byte[] decrypt(byte[] paramArrayOfByte, String paramString)
//  {
//    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
//      return null;
//    return doFinal(2, paramArrayOfByte, paramString);
//  }

  public static String doFinal(int paramInt, String paramString1, String paramString2)
  {
    try
    {
      SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramString2.getBytes(), "DES");
      Cipher localCipher = Cipher.getInstance("DES");
      localCipher.init(paramInt, localSecretKeySpec);	
      if (paramInt == 2);
      byte[] arrayOfByte2;
      for (byte[] arrayOfByte1 = Base64.decode(paramString1, 0); ; arrayOfByte1 = paramString1.getBytes("UTF-8"))
      {
        arrayOfByte2 = localCipher.doFinal(arrayOfByte1);
        if (paramInt != 2)
          break;
        return new String(arrayOfByte2);
      }
      String str = Base64.encodeToString(arrayOfByte2, 0);
      return str;
    }
    catch (Exception localException)
    {
      new StringBuilder("opmode=").append(paramInt).toString();
    }
    return null;
  }

//  public static byte[] doFinal(int paramInt, byte[] paramArrayOfByte, String paramString)
//  {
//    try
//    {
//      SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramString.getBytes(), "DES");
//      Cipher localCipher = Cipher.getInstance("DES");
//      localCipher.init(paramInt, localSecretKeySpec);
//      if (paramInt == 2)
//        paramArrayOfByte = Base64.decode(paramArrayOfByte, 0);
//      Object localObject = localCipher.doFinal(paramArrayOfByte);
//      if (paramInt != 2)
//      {
//        byte[] arrayOfByte = Base64.encode(localObject, 0);
//        localObject = arrayOfByte;
//      }
//      return localObject;
//    }
//    catch (Exception localException)
//    {
//      new StringBuilder("opmode=").append(paramInt).toString();
//    }
//    return (B)null;
//  }

  public static String encrypt(String paramString1, String paramString2)
  {
    if (paramString1.equals(""))
      return "";
    return doFinal(1, paramString1, paramString2);
  }

//  public static byte[] encrypt(byte[] paramArrayOfByte, String paramString)
//  {
//    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
//      return null;
//    return doFinal(1, paramArrayOfByte, paramString);
//  }

}