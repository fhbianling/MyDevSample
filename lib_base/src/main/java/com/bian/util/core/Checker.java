package com.bian.util.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * author 边凌
 * date 2017/7/27 17:31
 * 类描述：参数检查工具类
 */

public class Checker {
    private Checker() {
        throw new UnsupportedOperationException();
    }

    public static <T> boolean equals(Collection<T> t1,
                                     Collection<T> t2) {
        return t1 != null && t2 != null && t1.equals(t2);
    }

    public static boolean hasNullObj(Object... objects) {
        if (objects != null) {
            for (Object object : objects) {
                if (object == null) return true;
            }
            return false;
        }
        return true;
    }

    public static <T> boolean isEmpty(Collection<T> list) {

        return list == null || list.size() == 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static <T> boolean isIndexValid(List<T> list, int... index) {
        if (index == null || isEmpty(list)) {
            //如果list为空，即使不null，任何索引依然是无效的
            return false;
        }

        for (int i : index) {
            if (i < 0 || i >= list.size()) return false;
        }
        return true;
    }

    public static <T> boolean isIndexValid(T[] list, int... index) {
        if (index == null || isEmpty(list)) {
            //如果list为空，即使不null，任何索引依然是无效的
            return false;
        }

        for (int i : index) {
            if (i < 0 || i >= list.length) return false;
        }
        return true;
    }


    /**
     * 判断Activity是否被销毁
     */
    @SuppressLint("ObsoleteSdkInt")
    public static boolean isActivityDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity == null || activity.isDestroyed();
        }
        return activity == null || activity.isFinishing();
    }

    /**
     * 判断Activity是否被销毁
     */
    @SuppressLint("ObsoleteSdkInt")
    public static boolean isActivityDestroyed(Context context) {
        if (context == null) return true;
        if (!(context instanceof Activity)) return false;
        Activity activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        return activity.isFinishing();
    }

    public static <T> boolean orEquals(T src, Object... target) {
        if (target != null) {
            for (Object o : target) {
                if (Objects.equals(src, o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean andEquals(T src, Object... target) {
        if (target != null) {
            for (Object o : target) {
                if (!Objects.equals(src, o)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
