package com.common.android.db;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by weifeng on 15/12/2.
 */
public class CacheHelper {
    private static final String CACHE_NAME = "data_cache";

    private CacheHelper() {

    }

    private static ACache getACache(Context ctx){
        File dir = new File(ctx.getCacheDir().getParentFile(),CACHE_NAME);
        if(!dir.exists()){
            dir.mkdir();
        }
        return ACache.get(dir);
    }

    public static void putString(Context ctx, String key, String value) {
        getACache(ctx).put(key, value);
    }


    /**
     * @param ctx
     * @param key
     * @param value
     * @param time  单位：秒 内置ACache.TIME_DAY ACache.TIME_HOUR
     */
    public static void putString(Context ctx, String key, String value, int time) {
        getACache(ctx).put(key, value, time);
    }

    public static void putObj(Context ctx, String key, Serializable data) {
        getACache(ctx).put(key, data);
    }

    public static void putObj(Context ctx, String key, Serializable data, int time) {
        getACache(ctx).put(key, data, time);
    }

    public static void putJsonObj(Context ctx, String key, JSONObject jsonObject) {
        getACache(ctx).put(key, jsonObject);
    }

    public static void putJsonObj(Context ctx, String key, JSONObject jsonObject, int time) {
        getACache(ctx).put(key, jsonObject, time);
    }

    public static void putJsonArray(Context ctx, String key, JSONArray jsonArray) {
        getACache(ctx).put(key, jsonArray);
    }

    public static void putJsonArray(Context ctx, String key, JSONArray jsonArray, int time) {
        getACache(ctx).put(key, jsonArray, time);
    }


    public static void putInt(Context ctx, String key, int value) {
        putString(ctx, key, String.valueOf(value));
    }

    public static void putBoolean(Context ctx, String key, boolean value) {
        int data = value == true ? 1 : 0;
        putInt(ctx, key, data);
    }

    public static String getAsString(Context ctx, String key) {
        return getACache(ctx).getAsString(key);
    }

    public static Object getAsObject(Context ctx, String key) {
        return getACache(ctx).getAsObject(key);
    }

    public static JSONObject getAsJsonObj(Context ctx, String key) {
        return getACache(ctx).getAsJSONObject(key);
    }

    public static JSONArray getAsJsonArray(Context ctx, String key) {
        return getACache(ctx).getAsJSONArray(key);
    }

    public static Integer getAsInt(Context ctx, String key) {
        return Integer.parseInt(getAsString(ctx, key));
    }

    public static Boolean getAsBoolean(Context ctx, String key) {
        int value = getAsInt(ctx, key);
        return value == 1 ? true : false;
    }


    public static void clear(Context ctx, String key) {
        getACache(ctx).remove(key);
    }

    public static void clearAll(Context ctx) {
        getACache(ctx).clear();
    }


}
