package com.common.android.utils;


import android.text.TextUtils;

import com.alibaba.fastjson.TypeReference;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CommonUtils {
    public static String getSignData(String signKey, Object... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object value : params) {
            if (value != null) {
                sb.append(value).append("&");
            }
        }
        sb.append(signKey);

        try {
            return EncryptUtils.getMD5(sb.toString());
        } catch (Exception e) {
            Logger.e(e);
        }
        return "";
    }


    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static boolean stringCompare(String str1, String str2) {
        return TextUtils.equals(str1, str2);
    }

    public static boolean isEmpty(String content) {
        if (content != null && !"null".equalsIgnoreCase(content) && !TextUtils.isEmpty(content)) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Object[] arrays) {
        return arrays == null || arrays.length == 0;
    }

    /**
     * 计算字符的长度，尤其用于汉字
     */
    public static int getCharsLen(String text) {
        if (text == null) {
            return 0;
        }

        int len = 0;
        int nchar = 0;
        int templen = text.length();
        for (int i = 0; i < templen; i++) {
            nchar = text.charAt(i);
            if (nchar > 0x80) {
                len += 2;
            } else {
                len += 1;
            }
        }
        return len;
    }


    /**
     * 获得四位的随机数
     */
    public static int getRandom() {
        Random random = new Random();
        return (int) (random.nextDouble() * 9999);
    }

    public static double toDouble(String value, double defValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            Logger.e(e);
        }
        return defValue;
    }

    public static int toInteger(String str, int defValue) {
        if (!isEmpty(str)) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public static float toFloat(String str, int defValue) {
        if (!isEmpty(str)) {
            try {
                return Float.parseFloat(str);
            } catch (Exception e) {
            }
        }
        return defValue;
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 != null) {
            return str1.equals(str2);
        }
        return false;
    }


    public static boolean equals(Integer int1, Integer int2) {
        if (int1 == null && int2 == null) {
            return true;
        } else if (int1 != null) {
            return int1.equals(int2);
        }
        return false;
    }

    public static boolean equals(Long param1, Long param2) {
        if (param1 == param2 && param2 == null) {
            return true;
        } else if (param1 != null) {
            return param1.equals(param2);
        }
        return false;
    }

    public static boolean equals(Object actual, Object expected) {
        return actual == expected || (actual != null && actual.equals(expected));
    }

    public static String toJSON(Object object) {
        try {
            return com.alibaba.fastjson.JSON.toJSONString(object);
        } catch (Exception e) {

        }
        return "";
    }

    public static <T> T parseJson(String json, TypeReference<T> type) {
        if (json == null || type == null) {
            return null;
        }
        try {
            return com.alibaba.fastjson.JSON.parseObject(json, type);
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return com.alibaba.fastjson.JSON.parseObject(json, clazz);
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    public static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return com.alibaba.fastjson.JSON.parseArray(json, clazz);
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    public static String getMd5(String value) {
        try {
            return EncryptUtils.getMD5(value);
        } catch (Exception e) {
            Logger.e(e);
        }
        return value;
    }

    /**
     * @param width 总宽度
     */
    public static int getItemWidth(int width, int space, int count) {
        return (width - space * (count + 1)) / count;
    }

    public static boolean checkLatLon(String latitude, String longitude) {
        try {
            double lon = Double.parseDouble(longitude);
            double lat = Double.parseDouble(latitude);
            return checkLatLon(lat, lon);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean checkLatLon(double lat, double lon) {
        return (73.66 <= lon && lon <= 135.05) && (3.86 <= lat && lat <= 53.55);
    }

    /**
     * 多个Url字段转化为List
     */
    public static List<String> toImageUrlList(String str) {
        if (CommonUtils.isEmpty(str)) {
            return null;
        }
        String[] strings = str.split(",");
        List<String> list = new ArrayList<>();
        for (String url : strings) {
            list.add((url));
        }
        return list;
    }

    /**
     * list中内容拼接在一起
     *
     * @param split 分隔符
     */
    public static <T> String listToString(List<T> list, String split) {
        if (CommonUtils.isEmpty(list)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (T t : list) {
            sb.append(t).append(split);
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return null;
        }
    }

    /**
     * 删除列表中项目
     */
    public static <T> void removeListItem(List<T> list, T item) {
        if (list == null || item == null) {
            return;
        }
        for (T t : list) {
            if (item.equals(t)) {
                list.remove(t);
                return;
            }
        }
    }

    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public static String double2String(double d) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        } catch (Exception e) {
        }
        return String.format("%.2f", d);

    }

    //double相加
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    //double 相乘
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }


    private static SimpleDateFormat sdf = null;

    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "" : sdf.format(l * 1000);
    }

    /**
     *  将秒转化为 时分秒 格式
     *  t 标识时间
     */
    public static String formatLongToTimeStr(int t) {
        int hour = 0;
        int minute = 0;
        int second = t;
        if (second >= 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
            if (second == 60) {
                second = 0;
                minute += 1;
            }
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
            if (minute == 60) {
                minute = 0;
                hour += 1;
            }
        }
        String strTime = hour + ":" + minute + ":" + second;

        String[] timeArr = strTime.split(":");
        for (int i = 0; i < timeArr.length; i++) {
            if (timeArr[0].length() == 1) {
                timeArr[0] = "0" + timeArr[0];
            } else {
                timeArr[0] = timeArr[0];
            }
            if (timeArr[1].length() == 1) {
                timeArr[1] = "0" + timeArr[1];
            } else {
                timeArr[1] = timeArr[1];
            }
            if (timeArr[2].length() == 1) {
                timeArr[2] = "0" + timeArr[2];
            } else {
                timeArr[2] = timeArr[2];
            }
        }
        return timeArr[0] + "时" + timeArr[1] + "分" + timeArr[2] + "秒";
    }

    /**
     *  将秒转化为 分秒 格式
     *  t 标识时间
     */
    public static String formatToMinAndSec(int t) {
        int minute = 0;
        int second = t;
        if (second >= 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
            if (second == 60) {
                second = 0;
                minute += 1;
            }
        }
        String strTime = minute + ":" + second;
        String[] timeArr = strTime.split(":");
        for (int i = 0; i < timeArr.length; i++) {
            if (timeArr[0].length() == 1) {
                timeArr[0] = "0" + timeArr[0];
            } else {
                timeArr[0] = timeArr[0];
            }
            if (timeArr[1].length() == 1) {
                timeArr[1] = "0" + timeArr[1];
            } else {
                timeArr[1] = timeArr[1];
            }
        }
        return timeArr[0] + ":" + timeArr[1];
    }

    /**
     *  判断剩余时间
     *  大于一天   显示天数
     *  小于一天   大于一小时   显示小时数
     *  小于一天   小于一小时   显示分钟数
     *  小于一天   小于一小时   小于一分钟  显示描述
     *  @param endTime  要转化时间  单位：秒
     */
    public static String getEndTime (int endTime){
        String str;
        if (endTime <= 0) {
            str = "已结束";
        } else {
            int days = endTime / (3600 * 24);
            if (days > 0) {
                str = days + "天";
            } else {
                int hour = endTime / 3600;
                if (hour > 0) {
                    str = hour + "小时";
                } else {
                    int min = endTime / 60;
                    if (min > 0) {
                        str = min + "分钟";
                    } else {
                        str = endTime + "秒";
                    }
                }
            }
        }
        return str;
    }

    /**
     * 数字转汉子
     * @param position 阿拉伯数字
     * @param hasSymbol 是否需要顿号
     * @return
     */
    public static String getStringByNumber(int position, boolean hasSymbol){
        String str = "";
        if (position == 1) {
            str = "一";
        } else if (position == 2) {
            str = "二";
        }else if (position == 3) {
            str = "三";
        }else if (position == 4) {
            str = "四";
        }else if (position == 5) {
            str = "五";
        }else if (position == 6) {
            str = "六";
        }else if (position == 7) {
            str = "七";
        }else if (position == 8) {
            str = "八";
        }else if (position == 9) {
            str = "九";
        }else if (position == 10) {
            str = "十";
        }else if (position == 11) {
            str = "十一";
        }else if (position == 12) {
            str = "十二";
        }else if (position == 13) {
            str = "十三";
        }else if (position == 14) {
            str = "十四";
        }else if (position == 15) {
            str = "十五";
        }else if (position == 16) {
            str = "十六";
        }else if (position == 17) {
            str = "十七";
        }else if (position == 18) {
            str = "十八";
        }else if (position == 19) {
            str = "十九";
        }else if (position == 20) {
            str = "二十";
        }
        if (hasSymbol) {
            return str + "、";
        } else {
            return str;
        }
    }

    /**
     * 根据字符串转化为list
     * @param str        目标字符串
     * @param symbol     目标分隔符
     * @return
     */
    protected List<String> getImageListFromString(String str, String symbol) {
        List<String> imageList = new ArrayList<>();
        if (CommonUtils.isEmpty(str)) {
            return null;
        }

        String[] imgUrls = str.split(symbol);
        for (int i = 0; i < imgUrls.length; i++) {
            imageList.add(imgUrls[i]);
        }
        return imageList;
    }

    public static String mixPhoneNumber(String phoneNumber){
        if (isEmpty(phoneNumber) || phoneNumber.length() <= 6){
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length());
    }
}
