package com.bian.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


@SuppressWarnings("unused")
public abstract class AbsBaseDialog extends Dialog implements View.OnClickListener {

    public AbsBaseDialog(Context context) {
        this(context, R.style.AppDialog);
    }
    public AbsBaseDialog(Context context, int styleRes) {
        super(context, styleRes);
        setContentView(getLayoutResId());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSetting();
        initView();
    }

    protected abstract int getLayoutResId();

    protected abstract void initView();

    @SuppressWarnings("WeakerAccess")
    protected void initSetting() {
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 设置dialog宽度全屏
     */
    protected final void setFullScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams lp = window.getAttributes();
        Point outSize = new Point();
        display.getSize(outSize);
        lp.width = outSize.x;
        window.setAttributes(lp);
    }

    /**
     * 设置dialog位于屏幕底部
     */
    protected final void setAtBottom() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
        }
    }
}
