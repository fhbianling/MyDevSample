package com.bian.util.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * author 边凌
 * date 2017/8/25 16:42
 * 类描述：输入法，软键盘的相关工具类
 */

public class InputMethodUtil {
    private InputMethodUtil(){
      throw new UnsupportedOperationException();
    }

    /**
     * 输入法弹出或隐藏反转
     */
    public static void toggleSoftInput(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 软键盘是否可见
     */
    public static boolean isSoftInputVisible(@NonNull Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static void closeInputMethodIfVisible(View anchor) {
        if (isSoftInputVisible(anchor.getContext())) {
            closeSoftInputFromWindow(anchor.getContext(), anchor);
        }
    }

    /**
     * 从某个window关闭软键盘，需要传入该Window的某个View以拿到窗口token
     */
    public static void closeSoftInputFromWindow(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
