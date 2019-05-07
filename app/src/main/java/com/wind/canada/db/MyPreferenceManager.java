package com.wind.canada.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferenceManager {

    private Context context;

    private static MyPreferenceManager instance;

    private MyPreferenceManager(Context context) {
        this.context = context;
    }

    public static MyPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyPreferenceManager(context);
        }
        return instance;
    }

    public String getString(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public int getInteger(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, -1);
    }

    public int getInteger(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public boolean getBoolean(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
    public long getLong(String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue);
    }

    public boolean putInteger(String key, int value) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.putInt(key, value);
        settingsEditor.commit();
        return true;
    }

    /**
     * 将键值对写入默认的SharedPreferences 值是String类型的
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putString(String key, String value) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.putString(key, value);
        settingsEditor.commit();
        return true;
    }

    /**
     * 将键值对写入默认的SharedPreferences 值是 value类型的
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.putBoolean(key, value);
        settingsEditor.commit();
        return true;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public boolean putLong(String key, long value) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.putLong(key, value);
        settingsEditor.apply();
        return true;
    }



    public void putDouble(String fieldName, double fieldVaule) {
        throw new RuntimeException("还没有实现该方法");
    }

    public boolean remove(String key) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.remove(key);
        settingsEditor.commit();
        return true;
    }

    public void clear() {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.clear();
        settingsEditor.commit();
    }
}
