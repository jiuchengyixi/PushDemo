package com.common.android.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * 系统设备锁工具类
 */
public class WakeLockUtil {
    private static PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    public static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "HEMSService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    public static void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
