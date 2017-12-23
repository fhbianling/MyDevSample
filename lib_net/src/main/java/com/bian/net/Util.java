package com.bian.net;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * author 边凌
 * date 2017/11/20 15:39
 * 类描述：
 */
class Util {
    private final static int JSON_INDENT = 2;

    private Util() {
        throw new UnsupportedOperationException();
    }

    /**
     * 判断Activity是否被销毁
     */
    static boolean isActivityDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        return activity == null || activity.isFinishing();
    }

    static void v(String logTag, String msg) {
        if (!Api.sHttpLoggingEnable) return;
        Log.v(logTag, msg);
    }

    static void i(String logTag, String msg) {
        if (!Api.sHttpLoggingEnable) return;
        Log.i(logTag, msg);
    }

    static void printJson(String logTag, String msg) {
        v(logTag, getFormatJson(msg));
    }

    private static String getFormatJson(String jsonStr) {
        try {
            if (TextUtils.isEmpty(jsonStr)) return jsonStr;
            String jsonStrWrapper = jsonStr.trim();
            /*JSONObject和JSONArray的构造中自带缩进设计，具体看源码*/
            if (jsonStrWrapper.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStrWrapper);
                return jsonObject.toString(JSON_INDENT);
            }
            if (jsonStrWrapper.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStrWrapper);
                return jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException ignored) {
            return jsonStr;
        }
        return jsonStr;
    }
}
