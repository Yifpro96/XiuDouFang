package com.example.administrator.xiudoufang.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.xiudoufang.base.XiuDouFangApplication;

import java.util.Map;
import java.util.Set;

/**
 * @author WYF on 2018/8/5/005.
 */
public class PreferencesUtils {

    public static final String IS_SAVED_ACCOUNT = "is_saved_account";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String USER_ID = "user_id";
    public static final String DIAN_ID = "dian_id";
    public static final String TIME_REFRESH = "time_refresh";
    public static final String SEQUENTIAL_SCAN = "sequential_scan";

    private PreferencesUtils() {}

    public static SharedPreferences getPreferences() {
        return XiuDouFangApplication.getContext().getSharedPreferences(XiuDouFangApplication.class.getPackage().getName(), Context.MODE_PRIVATE);
    }

    public static void remove(String key) {
        getPreferences().edit().remove(key).apply();
    }

    public static void save(String key, Object value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        if (value instanceof String) {
            edit.putString(key, (String)value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer)value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float)value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
        }
        edit.apply();
    }

    public static <T> void save(Map<String, T> map) {
        SharedPreferences.Editor edit = getPreferences().edit();;
        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            if (value instanceof String) {
                edit.putString(key, (String)value);
            } else if (value instanceof Boolean) {
                edit.putBoolean(key, (Boolean)value);
            } else if (value instanceof Integer) {
                edit.putInt(key, (Integer)value);
            } else if (value instanceof Float) {
                edit.putFloat(key, (Float)value);
            } else if (value instanceof Long) {
                edit.putLong(key, (Long) value);
            } else if (value instanceof Set) {
                edit.putStringSet(key, (Set<String>) value);
            }
        }
        edit.apply();
    }
}
