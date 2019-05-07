package com.wind.canada;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.common.android.utils.AndroidUtils;
import com.common.android.utils.CrashHandler;
import com.common.android.utils.Logger;
import com.common.android.utils.fresco.FrescoUtils;
import com.wind.canada.config.Config;

import java.io.File;

/**
 * Created by yuanht on 2017/7/18.
 */

public class MyApplication extends Application {
    public static Context APP_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;

        initData();

        //BaiduMap init
//        SDKInitializer.initialize(APP_CONTEXT);

       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initData() {
        FrescoUtils.initOkHttp(APP_CONTEXT, getImageCacheDir(), Config.DEBUG);

//        JPushInterface.setDebugMode(Config.DEBUG);    // 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);            // 初始化 JPush

        Logger.setTAG(Config.TAG);
        Logger.setDebug(Config.DEBUG);
        initCrashHandler();
    }


    /**
     * 异常处理
     */
    private void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler.getInstance(getLogCacheDir().getAbsolutePath());
        crashHandler.init(getApplicationContext());
    }

    public static File getWebViewCacheDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.WEBVIEW_CACHE_DIR_NAME);
    }

    public static File getImageCacheDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.IMAGE_CACHE_DIR_NAME);
    }

    public static File getLogCacheDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.LOG_CACHE_DIR_NAME);
    }

    public static File getTempDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.TEMP_DIR);
    }

    public static File getVideoDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.VIDEO_DIR);
    }

    public static File getSettingsDir() {
        return AndroidUtils.initCacheDir(APP_CONTEXT, Config.SETTINGS_DIR_NAME);
    }
}
