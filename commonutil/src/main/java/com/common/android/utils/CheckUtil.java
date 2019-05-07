package com.common.android.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuanhongtao on 15/12/11.
 */
public class CheckUtil {
    public static boolean isEmpty(Object[] objs) {
        return objs == null || objs.length == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(Collection list) {
        return list == null || list.size() == 0;
    }

    /**
     * 是否是有效的密码
     */
    public static boolean isValidPassword(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        return isIntegerAndAlpha(pwd);
    }

    /**
     * 关于正则表达式的匹配
     * <p>
     * [\u4e00-\u9fa5] //匹配中文字符 [^(0-9)] ^[1-9]\d*$ //匹配正整数 ^[A-Za-z]+$
     * //匹配由26个英文字母组成的字符串 ^[A-Z]+$ //匹配由26个英文字母的大写组成的字符串 ^[a-z]+$
     * //匹配由26个英文字母的小写组成的字符串 ^[A-Za-z0-9]+$ //匹配由数字和26个英文字母组成的字符串
     * ^[A-Za-z0-9_]+$ //匹配由数字和26个英文字母, 下划线组成的字符串 ^[0-9a-zA-Z _-]+$
     * //数字,字母,空格,下划线 [^(a-zA-Z0-9\\u4e00-\\u9fa5)] //数字 字母 中文
     */

    private static final Pattern PHONE = Pattern.compile("^((1[3-9])+\\d{9})$");
    // private static final Pattern PHONE = Pattern
    // .compile("(^(\\d{3,4}-)?\\d{7,8})$|(^(13[0-9]|15[0-9]|18[0-9])\\d{8}$)");
    // private static final Pattern MAIL =
    // Pattern.compile("[\\w-]+@(\\w+.)+[a-z]{2,3}");
    private static final Pattern MAIL = Pattern
            .compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");

    private static final Pattern INTEGER = Pattern.compile("^[0-9]+$");
    private static final Pattern POSITIVE_INTEGER = Pattern
            .compile("^[1-9]\\d*$");
    private static final Pattern INTEGER_ALPHA = Pattern
            .compile("^[A-Za-z0-9]+$");
    private static final Pattern INTEGER_ALPHA_LINE = Pattern
            .compile("^[0-9a-zA-Z_]+$");
    private static final Pattern INTEGER_ALPHA_HANZI = Pattern
            .compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");

    private static final Pattern IP = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)($|(?!\\.$)\\.)){4}$");

    private static final Pattern MONEY = Pattern.compile("^(([1-9][0-9]*)|(([0]\\.\\d{0,2}|[1-9][0-9]*\\.\\d{0,2})))$");

    /**
     * 是否是QQ号
     */
    public static boolean isQqNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }

        if (number.matches("[0-9]+") && number.trim().length() > 4
                && number.trim().length() < 12) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 是否是整数
     */
    public static boolean isInteger(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = INTEGER.matcher(number);
        return match.matches();
    }

    /**
     * 是否是正整数
     */
    public static boolean isPositiveInteger(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = POSITIVE_INTEGER.matcher(number);
        return match.matches();
    }

    /**
     * 是否是数字和字母
     */
    public static boolean isIntegerAndAlpha(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = INTEGER_ALPHA.matcher(number);
        return match.matches();
    }

    /**
     * 是否是数字,字母,下划线
     */
    public static boolean isIntegerAndAlphaLine(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = INTEGER_ALPHA_LINE.matcher(number);
        return match.matches();
    }

    /**
     * 是否是数字 字母和汉字
     */
    public static boolean isIntegerAlphaHanzi(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = INTEGER_ALPHA_HANZI.matcher(number);
        return match.matches();
    }

    /**
     * 是否是EMAIL地址
     */
    public static boolean isMailAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }

        Matcher match = MAIL.matcher(address);
        return match.matches();
    }

    /**
     * 是否是手机号
     */
    public static boolean isPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        Matcher match = PHONE.matcher(number);
        return match.matches();
    }


    /**
     * 是否是IP地址
     */
    public static boolean isIP(String ip) {
        if (TextUtils.isEmpty(ip) || ip.length() < 7 || ip.length() > 15) {
            return false;
        }
        Matcher match = IP.matcher(ip);
        return match.matches();
    }

    public static boolean isMoney (String value) {
        Matcher match = MONEY.matcher(value);
        return match.matches();
    }
}
