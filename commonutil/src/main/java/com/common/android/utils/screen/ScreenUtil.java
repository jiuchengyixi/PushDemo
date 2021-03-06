package com.common.android.utils.screen;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;

import com.common.android.utils.Logger;

public class ScreenUtil {
	  /** 可调节的最小亮度值 */  
    public static final int MIN_BRIGHTNESS = 30;  
    /** 可调节的最大亮度值 */  
    public static final int MAX_BRIGHTNESS = 255;  
	 /** 
     * 获得当前系统的亮度模式 
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度 
     */  
    public static int getBrightnessMode(Context context) {  
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;  
        try {  
            brightnessMode = Settings.System.getInt(context.getContentResolver(),  
                Settings.System.SCREEN_BRIGHTNESS_MODE);  
        } catch (Exception e) {  
            //Log.e(AppConstants.TAG, "获得当前屏幕的亮度模式失败：", e);  
        }  
        return brightnessMode;  
    }  
  
    /** 
     * 设置当前系统的亮度模式 
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度 
     */  
    public static void setBrightnessMode(Context context, int brightnessMode) {  
        try {  
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode);  
        } catch (Exception e) {  
            //Log.e(AppConstants.TAG, "设置当前屏幕的亮度模式失败：", e);  
        }  
    }  
  
    /** 
     * 获得当前系统的亮度值： 0~255 
     */  
    public static int getSysScreenBrightness(Context context) {  
        int screenBrightness = MAX_BRIGHTNESS;  
        try {  
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
        } catch (Exception e) {  
            //Log.e(AppConstants.TAG, "获得当前系统的亮度值失败：", e);  
        }  
        return screenBrightness;  
    }  
  
    /** 
     * 设置当前系统的亮度值:0~255 
     */  
    public static void setSysScreenBrightness(Context context, int brightness) {  
        try {  
            ContentResolver resolver = context.getContentResolver();  
            Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);  
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);  
            resolver.notifyChange(uri, null); // 实时通知改变  
        } catch (Exception e) {  
            Logger.e("设置当前系统的亮度值失败：" + e);
        }  
    }  
  
    /** 
     * 设置屏幕亮度，这会反映到真实屏幕上 
     *  
     * @param activity 
     * @param brightness 
     */  
    public static void setActScreenBrightness(final Activity activity, final int brightness) {  
        final WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        lp.screenBrightness = brightness / (float) MAX_BRIGHTNESS;  
        activity.getWindow().setAttributes(lp);  
    } 
}
