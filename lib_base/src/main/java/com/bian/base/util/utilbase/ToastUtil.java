package com.bian.base.util.utilbase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * author 边凌
 * date 2016/7/4 0004 11:23
 * desc ${Toast工具类}
 */
@SuppressWarnings("unused")
public final class ToastUtil {

    private static final int NONE = -1;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static int sLayoutRes = NONE;
    private static int sTextViewId = NONE;

    private ToastUtil() {
        throw new UnsupportedOperationException();
    }

    public static void init(Context context) {
        ToastUtil.context = context.getApplicationContext();
    }

    public static void showToastLong(String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    @SuppressWarnings("WeakerAccess")
    public static void showToastShort(String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(int strRes) {
        showToast(context, context.getString(strRes), Toast.LENGTH_SHORT);
    }

    public static void showToastLong(int strRes) {
        showToast(context, context.getString(strRes), Toast.LENGTH_LONG);
    }

    /**
     * 展示一个toast,duration可以为{@link Toast#LENGTH_LONG}或{@link Toast#LENGTH_SHORT}，
     * 也可以为一个自定义的值(此时值不能去0，1)，单位为ms
     *
     */
    public static void showToast(Context mContext, String text, int duration) {
        Toast mToast = getToast(mContext, text, duration);
        if (duration == Toast.LENGTH_LONG || duration == Toast.LENGTH_SHORT) {
            mToast.show();
        } else {
            showToastOfSelfDuration(mToast, duration);
        }

    }

    private static void showToastOfSelfDuration(final Toast mToast, int duration) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mToast.show();
            }
        }, 0, duration + 100);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mToast.cancel();
                timer.cancel();
            }
        }, duration);
    }


    @SuppressLint("ShowToast")
    private static Toast getToast(Context mContext, String text, int duration) {
        Toast mToast;
        if (sLayoutRes != NONE && sTextViewId != NONE) {
            View inflate = LayoutInflater.from(mContext).inflate(sLayoutRes, null, false);
            TextView textView = (TextView) inflate.findViewById(sTextViewId);
            textView.setText(text);
            mToast = new Toast(mContext);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(duration);
            mToast.setView(inflate);
        } else {
            mToast = Toast.makeText(mContext, text, duration);
        }
        return mToast;
    }
    /**
     * 为Toast设置布局id和文字id
     *
     * @param layoutRes  自定义toast的布局文件
     * @param textViewId 该布局文件中用于展示Toast文字信息的textView的id
     */
    public static void setStyle(@LayoutRes int layoutRes, @IdRes int textViewId) {
        ToastUtil.sLayoutRes = layoutRes;
        ToastUtil.sTextViewId = textViewId;
    }

    public static void showToast(Context mContext, int resId, int duration) {
        showToast(mContext, mContext.getResources().getString(resId), duration);
    }

}