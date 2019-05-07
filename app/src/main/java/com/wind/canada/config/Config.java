package com.wind.canada.config;

import com.common.android.db.ACache;
import com.wind.canada.BuildConfig;

/**
 * Created by yuanht on 2017/5/22.
 */
public class Config {
    /**
     * 是否是测试版
     */
    public static final boolean IS_DEV = BuildConfig.APPLICATION_ID.endsWith(".dev");

    public static final boolean DEBUG = BuildConfig.DEBUG || IS_DEV;

    public static final boolean SAVE_LOG = IS_DEV;
    public static final boolean UPLOAD_ERROR_LOG = true;

    public static final String TAG = "Canada";

    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_LONG_NO_S = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MONTH = "yyyy年MM月";
    public static final String DATE_FORMAT_DAY = "yyyy年MM月dd日";
    public static final String DATE_FORMAT_TIME_ONLY = "HH:mm";
    public static final String DATE_FORMAT_FOR_TIME_SELECT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_MIN_AND_SEC = "mm:ss";
    public static final String DATE_FORMAT_HOUR_AND_MIN_SEC = "hh:mm:ss";
    public static final String DATE_FORMAT_PUBLISH = "yyyy/MM/dd hh:mm";
    public static final String DATE_FORMAT_PUBLISH_DATE = "yyyy/MM/dd";
    public static final String DATE_FORMAT_PUBLISH_CN = "yyyy年MM月dd日 hh:mm";
    public static final String DATE_FORMAT_MONTH_DAY = "MM月dd日";



    public static final int AVATAR_MAX_WIDTH = 200;
    public static final int AVATAR_MAX_HEIGHT = 200;

    public static final int IMAGE_MAX_WIDTH = 1500;
    public static final int IMAGE_MAX_HEIGHT = 1500;

    public static final long IMAGE_FILE_MAX_LENGTH = 10 * 1024 * 1024;
    public static final long IMAGE_FILE_LENGTH = 2 * 1024 * 1024;

    public static final int ORDER_DELAY_TIME = 24 * 60 * 60;

    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 轮播图片自动滚动间隔
     */
    public static final int PICTURE_SCROLL_INTERVAL = 5 * 1000;


    public static final String LOG_CACHE_DIR_NAME = "log";
    public static final String SETTINGS_DIR_NAME = "settings";
    public static final String IMAGE_CACHE_DIR_NAME = "image";
    public static final String AVATAR_TEMP_NAME = "avatar.tmp";
    public static final String WEBVIEW_CACHE_DIR_NAME = "webViewCache";
    public static final String TEMP_DIR = "temp";
    public static final String VIDEO_DIR = "video";



    public static final int OS_TYPE_ANDROID = 2;


    public static final String EXTRA_LOAD_URL = "load_url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_SHOW_TITLEBAR = "show_titlebar";
    public static final String EXTRA_LINK_TYPE = "link_type";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_COUNT = "count";
    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_START_FROM = "start_from";
    public static final String EXTRA_VOTE_BIND_AP = "bind_ap";
    public static final String EXTRA_ART_TYPE = "art_type";

    public static final String EXTRA_MODEL = "model";   //传值的对象是一个model
    public static final String EXTRA_LIST = "list";   //传值的对象是一个list

    //三方登录
    public static final String EXTRA_THIRD_TYPE = "third_type";
    public static final String EXTRA_OPENID = "open_id";
    public static final String EXTRA_NICK_NAME = "nickname";
    public static final String EXTRA_AVATAR_URL = "avatar_url";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_CODE = "code";


}
