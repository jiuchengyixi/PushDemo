package com.common.android.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by yuanht on 2017/6/29.
 */

public class DESHelper {
    public static String decryptKey = "GeenWinZhiFo";

    /**
     * DES加密
     * @param data		加密数据
     * @param key		加密签名
     * @return	密文
     */
    public static String encode(String data, String key) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return Base64Util.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES算法，解密
     *
     * @param key
     *            解密私钥，长度不能够小于8位
     * @param data
     *            待解密字符串
     * @return 解密后内容
     */
    public static String decode(String data, String key) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            return new String(cipher.doFinal(Base64Util.decode(data)),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    public static void main(String[] args) {
        System.out.println(decode("0/Junw1VqfQ=", decryptKey));
    }
}
