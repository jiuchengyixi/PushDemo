package com.wind.canada.db;

import android.app.Activity;
import android.content.Context;
import com.common.android.db.CacheHelper;

public class DBManager {
    private static DBManager instance;
    private Context mContext;
    private MyPreferenceManager mPreferenceManager;


    private DBManager(Context context) {
        mContext = context;
        mPreferenceManager = MyPreferenceManager.getInstance(mContext);
    }

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    public final class OPKey {
        public static final String FIRST_LOADED = "first_loaded";
        public static final String HOST = "host";
        public static final String USER_ID = "user_id";
        public static final String USER_TYPE = "user_type";
        public static final String USER_LOGIN_TYPE = "user_login_type";
        public static final String NICKNAME = "nickname";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String USER_AVATAR = "user_avatar";
        public static final String USER_CODE = "user_code";
        public static final String USER_SEX = "user_sex";
        public static final String USER_PHONE = "user_phone";

    }

    // ---------------Preference operation BEGIN--------------------------------
    public boolean setFirstLoaded() {
        return saveValue(OPKey.FIRST_LOADED, "1");
    }

    /**
     * 是否是第一次
     */
    public boolean isFirstLoaded() {
        return "1".equals(getValue(OPKey.FIRST_LOADED));
    }

    //userId
    public boolean setUserId(int userId) {
        return saveIntValue(OPKey.USER_ID, userId);
    }

    public int getUserId() {
        return getIntValue(OPKey.USER_ID);
    }

    public boolean setPhone(String phone) {
        return saveValue(OPKey.USER_PHONE, phone);
    }

    public String getPhone() {
        return getValue(OPKey.USER_PHONE);
    }

    public boolean setUserCode(String value) {
        return saveValue(OPKey.USER_CODE, value);
    }

    public String getUserCode() {
        return getValue(OPKey.USER_CODE);
    }

    public boolean setUserType(int value) {
        return saveIntValue(OPKey.USER_TYPE, value);
    }

    public int getUserType() {
        return getIntValue(OPKey.USER_TYPE);
    }


    public Integer getUserLoginType() {
        return getIntValue(OPKey.USER_LOGIN_TYPE);
    }

    public boolean setUserLoginType(Integer value) {
        return saveIntValue(OPKey.USER_LOGIN_TYPE, value);
    }

    public boolean setUserAvatar(String value) {
        return saveValue(OPKey.USER_AVATAR, value);
    }

    public String getUserAvatar() {
        return getValue(OPKey.USER_AVATAR);
    }

    public boolean setNickName(String value) {
        return saveValue(OPKey.NICKNAME, value);
    }

    public String getNickName() {
        return getValue(OPKey.NICKNAME);
    }

    public boolean setAccessToken(String token) {
        return saveValue(OPKey.ACCESS_TOKEN, token);
    }

    public String getAccessToken() {
        return getValue(OPKey.ACCESS_TOKEN);
    }

    public String getHost() {
        return getValue(OPKey.HOST);
    }

    public void setHost(String host) {
        saveValue(OPKey.HOST, host);
    }

    public void setLogout(Activity activity) {
        setUserId(-1);
        setAccessToken("");
        setUserAvatar("");
        setNickName("");
        CacheHelper.clearAll(activity);

        //TODO 退出三方登录
    }

    public boolean isLogin() {
        return getUserId() > 0;
    }


    public boolean saveValue(String key, String value) {
        return mPreferenceManager.putString(key, value);
    }

    public boolean saveIntValue(String key, Integer value) {
        return mPreferenceManager.putInteger(key, value);
    }

    public String getValue(String key) {
        return mPreferenceManager.getString(key);
    }

    public Integer getIntValue(String key) {
        return mPreferenceManager.getInteger(key);
    }

    public boolean saveBooleanValue(String key, Boolean value) {
        return mPreferenceManager.putBoolean(key, value);
    }

    public Boolean getBooleanValue(String key) {
        return mPreferenceManager.getBoolean(key);
    }
    // -----------------Preference operation END--------------------------------
}
