package com.common.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    /**
     * HMAC-SHA1加密
     *
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] HMACSHA1Encode(byte[] source, byte[] key)
            throws InvalidKeyException, NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String HMAC_SHA1 = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(signingKey);
        return mac.doFinal(source);
    }

    /**
     * MD5加密 结果字符串
     *
     * @param source
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5Encode(byte[] source) throws NoSuchAlgorithmException {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = MessageDigest
                .getInstance("MD5");
        md.update(source);
        byte tmp[] = md.digest();
        char str[] = new char[16 * 2];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        s = new String(str);
        return s;
    }

    public static String SHA1Encode(String strMing) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(strMing.getBytes("UTF-8"));
            byte[] result = md.digest();
            StringBuffer sb = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < result.length; i++) {
                String shaHex = Integer.toHexString(result[i] & 0xFF);
                if (shaHex.length() < 2) {
                    sb.append(0);
                }
                sb.append(shaHex);
            }
            return sb.toString();
        } catch (Exception e) {
            Logger.e("SHA1Encode, strMing=" + strMing + ", error=" + e);
        }
        return null;
    }

    /**
     * MD5加密
     */
    public static String getMD5(String val) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes("UTF-8"));
        byte[] m = md5.digest();
        return toHexString(m);
    }

    /**
     * 转16进制字符
     */
    private static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            sb.append(Integer.toHexString((b[i] & 0xFF) | 0x100)
                    .substring(1, 3));
        }
        return sb.toString();
    }
}
