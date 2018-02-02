package com.bian.util;

import android.app.Application;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import com.bian.util.core.L;
import com.bian.util.core.SharedPrefUtil;
import com.bian.util.core.ToastUtil;
import com.bian.util.throwable.ThrowableHandler;
import com.squareup.leakcanary.LeakCanary;

/**
 * author 边凌
 * date 2017/6/13 10:55
 * 类描述：管理base包下util的初始化，资源释放等
 */

public class BaseUtilManager {
    public static boolean DEBUG;

    private BaseUtilManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * 初始化整个base Module，需要在Application的onCreate方法中调用
     *
     * @param application application
     * @param debug       是否为debug模式
     */
    public static void init(Application application, boolean debug) {
        DEBUG = debug;
        initLeakCanary(application);
        SharedPrefUtil.init(application);
        ThrowableHandler.init(application);
        ToastUtil.init(application);
        L.setDEBUG(debug);
    }
    public static void setDebugTag(String tag) {
        L.setTAG(tag);
    }

    private static void initLeakCanary(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }
        LeakCanary.install(application);
    }

    public static void setToastStyle(@LayoutRes int layoutRes, @IdRes int textViewId) {
        ToastUtil.setStyle(layoutRes, textViewId);
    }
}
