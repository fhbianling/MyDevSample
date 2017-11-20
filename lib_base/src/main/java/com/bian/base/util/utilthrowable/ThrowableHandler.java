package com.bian.base.util.utilthrowable;

import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import com.bian.base.util.utilbase.L;
import com.bian.base.util.utilbase.ToastUtil;

/**
 * author 边凌
 * date 2017/6/12 15:09
 * desc ${异常处理类}
 */

public class ThrowableHandler {
    private ThrowableHandler() {
        throw new UnsupportedOperationException();
    }

    public static void init(Context context) {
        CrashReport.initCrashReport(context, "d245ea1029", false);
    }

    /**
     * 打印错误信息并上报BugHd，同时针对特定异常弹出提示信息
     *
     * @param t Throwable
     */
    public static void handle(Throwable t) {
        if (t != null) {
            L.printError(t);
            if (t instanceof SocketTimeoutException) {
                ToastUtil.showToastShort("网络连接超时");
            } else if (t instanceof ConnectException ||
                    t instanceof NoRouteToHostException) {
                ToastUtil.showToastShort("服务器暂时无法连接，请稍后重试");
            }
        } else {
            L.e("要处理的异常对象为空");
        }
    }

    public static void onlyLog(Throwable throwable) {
        L.printError(throwable);
    }
}
