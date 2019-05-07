package com.common.android.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanht on 2017/12/15.
 */

public class JSONUtils {
    public static boolean isPrintException = false;

    private JSONUtils() {
        throw new AssertionError();
    }

    public static Long getLong(JSONObject jsonObject, String key, Long defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }
        try {
            return jsonObject.getLong(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static Long getLong(String jsonData, String key, Long defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getLong(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static long getLong(JSONObject jsonObject, String key, long defaultValue) {
        return getLong(jsonObject, key, (Long) defaultValue);
    }

    public static long getLong(String jsonData, String key, long defaultValue) {
        return getLong(jsonData, key, (Long) defaultValue);
    }

    public static Integer getInt(JSONObject jsonObject, String key, Integer defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }
            return defaultValue;
        }
    }

    public static Integer getInt(String jsonData, String key, Integer defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getInt(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static int getInt(JSONObject jsonObject, String key, int defaultValue) {
        return getInt(jsonObject, key, (Integer) defaultValue);

    }

    /**
     * @param jsonData
     * @param key
     * @param defaultValue
     * @return
     * @see JSONUtils#getInt(String, String, Integer)
     */

    public static int getInt(String jsonData, String key, int defaultValue) {
        return getInt(jsonData, key, (Integer) defaultValue);
    }

    public static Double getDouble(JSONObject jsonObject, String key, Double defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static Double getDouble(String jsonData, String key, Double defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getDouble(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static double getDouble(JSONObject jsonObject, String key, double defaultValue) {
        return getDouble(jsonObject, key, (Double) defaultValue);

    }

    public static double getDouble(String jsonData, String key, double defaultValue) {
        return getDouble(jsonData, key, (Double) defaultValue);
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }
            return defaultValue;
        }
    }

    public static String getString(String jsonData, String key, String defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getString(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static String getStringCascade(JSONObject jsonObject, String defaultValue, String... keyArray) {
        if (jsonObject == null || ArrayUtils.isEmpty(keyArray)) {
            return defaultValue;
        }

        String data = jsonObject.toString();
        for (String key : keyArray) {
            data = getStringCascade(data, key, defaultValue);
            if (data == null) {
                return defaultValue;
            }
        }

        return data;
    }

    public static String getStringCascade(String jsonData, String defaultValue, String... keyArray) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        String data = jsonData;

        for (String key : keyArray) {
            data = getString(data, key, defaultValue);
            if (data == null) {
                return defaultValue;
            }
        }

        return data;
    }

    public static String[] getStringArray(JSONObject jsonObject, String key, String[] defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            JSONArray statusArray = jsonObject.getJSONArray(key);
            if (statusArray != null) {
                String[] value = new String[statusArray.length()];
                for (int i = 0; i < statusArray.length(); i++) {
                    value[i] = statusArray.getString(i);
                }

                return value;
            }
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }

        return defaultValue;
    }

    public static String[] getStringArray(String jsonData, String key, String[] defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getStringArray(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static List<String> getStringList(JSONObject jsonObject, String key, List<String> defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            JSONArray statusArray = jsonObject.getJSONArray(key);
            if (statusArray != null) {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < statusArray.length(); i++) {
                    list.add(statusArray.getString(i));
                }

                return list;
            }
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }

        return defaultValue;
    }

    public static List<String> getStringList(String jsonData, String key, List<String> defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getStringList(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key, JSONObject defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static JSONObject getJSONObject(String jsonData, String key, JSONObject defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getJSONObject(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static JSONObject getJSONObjectCascade(JSONObject jsonObject, JSONObject defaultValue, String... keyArray) {
        if (jsonObject == null || ArrayUtils.isEmpty(keyArray)) {
            return defaultValue;
        }

        JSONObject js = jsonObject;

        for (String key : keyArray) {
            js = getJSONObject(js, key, defaultValue);
            if (js == null) {
                return defaultValue;
            }
        }

        return js;
    }

    public static JSONObject getJSONObjectCascade(String jsonData, JSONObject defaultValue, String... keyArray) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getJSONObjectCascade(jsonObject, defaultValue, keyArray);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject jsonObject, String key, Boolean defaultValue) {
        if (jsonObject == null || StringUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static boolean getBoolean(String jsonData, String key, Boolean defaultValue) {
        if (StringUtils.isEmpty(jsonData)) {
            return defaultValue;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getBoolean(jsonObject, key, defaultValue);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return defaultValue;
        }
    }

    public static Map<String, String> getMap(JSONObject jsonObject, String key) {
        return JSONUtils.parseKeyAndValueToMap(JSONUtils.getString(jsonObject, key, null));
    }

    public static Map<String, String> getMap(String jsonData, String key) {
        if (jsonData == null) {
            return null;
        }

        if (jsonData.length() == 0) {
            return new HashMap<String, String>();
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return getMap(jsonObject, key);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map<String, String> parseKeyAndValueToMap(JSONObject sourceObj) {
        if (sourceObj == null) {
            return null;
        }

        Map<String, String> keyAndValueMap = new HashMap<String, String>();
        for (Iterator iter = sourceObj.keys(); iter.hasNext(); ) {
            String key = (String) iter.next();
            MapUtil.putMapNotEmptyKey(keyAndValueMap, key, getString(sourceObj, key, ""));
        }

        return keyAndValueMap;
    }

    public static Map<String, String> parseKeyAndValueToMap(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(source);
            return parseKeyAndValueToMap(jsonObject);
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static List<String> parseValueToList(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(source);
            List<String> list = new ArrayList<>();
            for (Iterator iter = jsonObject.keys(); iter.hasNext(); ) {
                String key = (String) iter.next();
                if (jsonObject.get(key)==null) {
                    list.add("");
                } else {
                    list.add((String) jsonObject.get(key));
                }

            }
            return list;
        } catch (JSONException e) {
            if (isPrintException) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
