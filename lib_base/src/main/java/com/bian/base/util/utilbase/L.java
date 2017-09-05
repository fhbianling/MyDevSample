package com.bian.base.util.utilbase;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.Locale;

/**
 * author 边凌
 * date 2017/6/13 10:51
 * 类描述：日志工具类
 * 包含线程打印,方法调用栈打印和Json打印功能
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class L {
    private final static int E = 1;
    private final static int D = 2;
    private final static int I = 3;
    private final static int V = 4;
    private final static int JSON = 5;
    private final static int JSON_INDENT = 2;
    private static String TAG = "LOG";
    private volatile static boolean DEBUG = true;

    private L() {
        throw new UnsupportedOperationException();
    }

    public static void setTAG(String TAG) {
        L.TAG = TAG;
    }

    public static void setDEBUG(boolean DEBUG) {
        L.DEBUG = DEBUG;
    }

    public static void v(Object message) {
        v(TAG, message);
    }

    public static void v(String tag, Object message) {
        log(V, tag, message, false, true);
    }

    public static void vThread(Object message) {
        vThread(TAG, message);
    }

    public static void vThread(String tag, Object message) {
        log(V, tag, message, true, true);
    }

    public static void i(Object message) {
        i(TAG, message);
    }

    public static void i(String tag, Object message) {
        log(I, tag, message, false, true);
    }

    public static void iThread(Object message) {
        iThread(TAG, message);
    }

    public static void iThread(String tag, Object message) {
        log(I, tag, message, true, true);
    }

    public static void e(Object message) {
        e(TAG, message);
    }

    public static void e(String tag, Object message) {
        log(E, tag, message, false, true);
    }

    public static void eThread(Object message) {
        eThread(TAG, message);
    }

    public static void eThread(String tag, Object message) {
        log(E, tag, message, true, true);
    }

    public static void d(Object message) {
        d(TAG, message);
    }

    public static void d(String tag, Object message) {
        log(D, tag, message, false, true);
    }

    public static void dThread(Object message) {
        dThread(TAG, message);
    }

    public static void dThread(String tag, Object message) {
        log(D, tag, message, true, true);
    }

    public static void printError(Throwable e) {
        PrintStream err = System.err;
        StackTraceElement[] stackTrace = e.getStackTrace();
        err.println("L.printError():\n" + e +","+ e.getMessage());
        for (StackTraceElement stackTraceElement : stackTrace) {
            err.println("\tat " + stackTraceElement);
        }
    }

    private static void log(int level, String tag, Object message, boolean shouldLogThread, boolean shouldTraceMessage) {
        if (!DEBUG) {
            return;
        }

        try {
            String logContent = msgWrapper(message.toString(), shouldLogThread, shouldTraceMessage);
            switch (level) {
                case E:
                    Log.e(tag, logContent);
                    break;
                case D:
                    Log.d(tag, logContent);
                    break;
                case I:
                    Log.i(tag, logContent);
                    break;
                case V:
                    Log.v(tag, logContent);
                    break;
                case JSON:
                    Log.d(tag, logContent);
                    break;
            }

        } catch (NullPointerException e) {
            printError(e);
        }
    }

    private static String msgWrapper(String message, boolean shouldLogThread, boolean shouldTraceMessage) {
        String traceMessage = shouldTraceMessage ? getTraceMsg() : "";
        String threadMessage = shouldLogThread ? getThreadMsg() : "";
        return traceMessage + threadMessage + message;
    }

    private static String getThreadMsg() {
        return String.format(Locale.CHINA, "[ %s ] ", Thread.currentThread().getName());
    }

    private static String getTraceMsg() {
        StackTraceElement targetStackTrace = getTargetStackTrace();
        if (targetStackTrace == null) {
            return "";
        }
        return String.format(Locale.CHINA, "(%s:%d) ", targetStackTrace.getFileName(), targetStackTrace.getLineNumber());
    }

    private static StackTraceElement getTargetStackTrace() {
        StackTraceElement stackTraceElement = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        /*栈中前两条都是调用getStackTrace方法产生的固定信息，调用该方法也会将该方法的调用记录压入栈，因此过滤掉*/
        boolean isJVMTrace;
        int count = 0;
        for (StackTraceElement traceElement : stackTrace) {
            boolean isLogMethod = traceElement.getClassName().equals(L.class.getName());
            isJVMTrace = (count < 2);
            if (!isLogMethod && !isJVMTrace) {
                stackTraceElement = traceElement;
                break;
            }
            count++;
        }

        return stackTraceElement;
    }

    public static void printJson(String jsonStr) {
        printJson(TAG, jsonStr);
    }

    public static void printJson(String tag, String jsonStr) {
        String formatJson = getFormatJson(jsonStr);
        if (!TextUtils.isEmpty(formatJson)) {
            log(JSON, tag, formatJson, false, false);
        } else {
            log(JSON, tag, jsonStr, false, false);
        }
    }

    private static String getFormatJson(String jsonStr) {
        try {
            jsonStr = jsonStr.trim();
            /*JSONObject和JSONArray的构造中自带缩进设计，具体看源码*/
            if (jsonStr.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                return jsonObject.toString(JSON_INDENT);
            }
            if (jsonStr.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                return jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException ignored) {

        }
        return "";
    }
}
