package com.common.android.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by yuanht on 2018/7/17.
 * 各种数据格式化的工具类
 */
public class FormatUtils {
    /**
     * 字符串转成时间
     *
     * @param format yyyy-MM-dd HH:mm:ss
     * @param str
     */
    public static Calendar getCalendar(String format, String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Date date = sdf.parse(str);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 文件大小处理
     */
    public static String parseFileSize(long length) {
        String size = "";
        DecimalFormat df = new DecimalFormat("#.00");
        if (length < 1024) {
            size = df.format((double) length) + "B";
        } else if (length < 1048576) {
            size = df.format((double) length / 1024) + "KB";
        } else if (length < 1073741824) {
            size = df.format((double) length / 1048576) + "MB";
        } else {
            size = df.format((double) length / 1073741824) + "GB";
        }
        return size;
    }

    /**
     * 文件大小处理
     */
    public static String parseSize(long size) {
        BigDecimal b = new BigDecimal(((double) size) / 1024 / 1024);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
    }

    /**
     * 手机号中间位数mask
     */
    public static String mixPhoneNumber(String phoneNumber) {
        if (CommonUtils.isEmpty(phoneNumber) || phoneNumber.length() <= 6) {
            return phoneNumber;
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7, phoneNumber.length());
    }

    /**
     * 金额，万元过滤，例如 2.3万元
     */
    public static String getMoneyFormatString(float money, int minMoney) {
        if (money % minMoney == 0) {
            return String.valueOf(money / minMoney);
        }

        float d = money / minMoney;
        try {
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(d);
        } catch (Exception e) {
        }
        return String.format("%.1f", d);
    }

    /**
     * 金额，保留小数后两位
     */
    public static String formatMoney(float money) {
        if (money == 0) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(money);
    }

    /**
     * 获得剩余天数
     */
    public static int getRemainDay(int endTimestamp) {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime >= endTimestamp) {
            return 0;
        } else {
            long delay = endTimestamp - currentTime;
            int day = 24 * 60 * 60;
            if (delay % day == 0) {
                return (int) (delay / day);
            } else {
                return (int) (delay / day) + 1;
            }
        }
    }

    /**
     * 视频分秒
     */
    public static String getVideoDuration(int timestamp) {
        if (timestamp == 0) {
            return "00:00";
        }
        if (timestamp < 60) {
            return "00:" + timestamp;
        }
        int minute = timestamp / 60;
        int second = timestamp % 60;

        String strMinute, strSecond;
        if (minute < 10) {
            strMinute = "0" + minute;
        } else {
            strMinute = String.valueOf(minute);
        }
        if (second < 10) {
            strSecond = "0" + second;
        } else {
            strSecond = String.valueOf(second);
        }

        return strMinute + ":" + strSecond;
    }

    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 获得剩余时分秒
     */
    public static String getRemainHMS(int endTimestamp) {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime >= endTimestamp) {
            return 0 + "时";
        } else {
            long delay = endTimestamp - currentTime;
            int hour = (int) (delay / (60 * 60));
            delay = delay - hour * 60 * 60;
            int minutes = (int) (delay / 60);
            int second = (int) (delay - minutes * 60);
            return hour + "时" + minutes + "分" + second + "秒";
        }
    }

    /**
     * 保留两位小数
     */
    public static String double2String(double d) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(d);
        } catch (Exception e) {
        }
        return String.format("%.2f", d);

    }

    private static SimpleDateFormat sdf = null;

    /**
     * 时间戳转时间
     */
    public static String formatTimeStamp(long l, String strPattern) {
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
        if (l < 2000000000) {
            l = l * 1000;
        }
        return sdf == null ? "" : sdf.format(l);
    }

    /**
     * 时间转时间戳
     */
    public static long dateToTimeStamp(String str, String format) {
        long date = 0;
        if (str != null && !TextUtils.isEmpty(str)) {
            try {
                date = new java.text.SimpleDateFormat(format).parse(str).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年


    public static String getTimeFormatText(String dateStr, String format) {
        if (dateStr != null && !TextUtils.isEmpty(dateStr)) {
            try {
                return getTimeFormatText(new java.text.SimpleDateFormat(format).parse(dateStr).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getTimeFormatText(long date) {
        return getTimeFormatText(new Date(date));
    }

    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(Date date) {
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    /**
     * 获得UTC时间
     */
    public static long getUtcTime() {
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        // 之后调用cal.get(int x)或cal.getTimeInMillis()方法所取得的时间即是UTC标准时间。
        return cal.getTimeInMillis();
    }


}
