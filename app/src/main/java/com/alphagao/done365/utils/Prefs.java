package com.alphagao.done365.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Alpha on 2017/4/6.
 */

public class Prefs {
    private static Prefs instance;
    private SharedPreferences preferences;
    private static final String FILE_NAME = "APP_CONFIG";

    public enum DataType {
        INTEGEAR, LONG, FLOAT, BOOLEAN, STRING, STRINGSET
    }

    public static Prefs getInstance(Context context) {
        if (instance == null) {
            synchronized (Prefs.class) {
                if (instance == null) {
                    instance = new Prefs(context, FILE_NAME);
                }
            }
        }
        return instance;
    }

    public static Prefs getUserInstance(Context context, Long userId) {
        return new Prefs(context, userId.toString());

    }

    private Prefs(Context context, String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private void put(String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.commit();
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    public void putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
    }

    public void putLong(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public void putString(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public void putStringSet(String key, Set<String> value) {
        preferences.edit().putStringSet(key, value).commit();
    }

    public Object get(String key, DataType type) {
        switch (type) {
            case INTEGEAR:
                return preferences.getInt(key, -1);
            case LONG:
                return preferences.getLong(key, -1L);
            case FLOAT:
                return preferences.getFloat(key, -1f);
            case BOOLEAN:
                return preferences.getBoolean(key, false);
            case STRING:
                return preferences.getString(key, "");
            case STRINGSET:
                return preferences.getStringSet(key, null);
            default:
                return null;
        }
    }

    public int getInt(String key) {
        return (int) get(key, DataType.INTEGEAR);
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public float getFloat(String key) {
        return (float) get(key, DataType.FLOAT);
    }

    public float getFloat(String key, float defValue) {
        return preferences.getFloat(key, defValue);
    }

    public long getLong(String key) {
        return Long.valueOf(get(key, DataType.LONG).toString());
    }

    public long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    public boolean getBoolean(String key) {
        return (boolean) get(key, DataType.BOOLEAN);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public String getString(String key) {
        return (String) get(key, DataType.STRING);
    }

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public Set<String> getStringSet(String key) {
        return (Set<String>) get(key, DataType.STRINGSET);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return preferences.getStringSet(key, defValue);
    }
}
