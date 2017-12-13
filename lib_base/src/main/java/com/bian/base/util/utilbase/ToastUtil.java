package com.bian.base.util.utilbase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
            View inflate = getMsgContentView(mContext, text);
            mToast = new Toast(mContext);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(duration);
            mToast.setView(inflate);
        } else {
            mToast = Toast.makeText(mContext, text, duration);
        }
        return mToast;
    }

    @NonNull
    private static View getMsgContentView(Context mContext, String text) {
        View inflate = LayoutInflater.from(mContext).inflate(sLayoutRes, null, false);
        TextView textView = inflate.findViewById(sTextViewId);
        textView.setText(text);
        return inflate;
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

    public static void showToastInBackground(Context context, String msg, int duration) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return;
        WindowManager.LayoutParams lp;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
        }
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final View msgContentView = getBackgroundMsgContentView(context, msg);
        wm.addView(msgContentView, lp);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                wm.removeViewImmediate(msgContentView);
            }
        };
        timer.schedule(timerTask, duration);
    }

    @NonNull
    private static View getBackgroundMsgContentView(Context context, String msg) {
        if (sLayoutRes != NONE && sTextViewId != NONE) {
            return getMsgContentView(ToastUtil.context, msg);
        } else {
            return getDefaultToastView(context, msg);
        }
    }

    @NonNull
    private static View getDefaultToastView(Context context, String msg) {
        TextView textView = new TextView(context);
        textView.setBackgroundColor(Color.parseColor("#33575757"));
        textView.setPadding(15, 5, 15, 5);
        int textColor = context.getResources().getColor(android.R.color.background_light);
        textView.setBackgroundColor(Color.parseColor("#eeaaaaaa"));
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setText(msg);
        return textView;
    }

}