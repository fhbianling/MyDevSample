package com.bian.base.baseclass;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * author 边凌
 * date 2016/11/2 14:50
 * desc ${PopupWindow基类}
 */
public abstract class BasePopupWindow extends PopupWindow {
    private final static float DEFAULT_ALPHA = 0.55f;
    private final boolean shouldBackAlpha;
    protected float alpha = DEFAULT_ALPHA;
    private Activity mContext;

    protected Activity getActivity(){
        return mContext;
    }

    public BasePopupWindow(Activity mContext) {
        this(mContext, true, false);
    }

    public BasePopupWindow(Activity mContext, boolean inflateImmediately) {
        this(mContext, inflateImmediately, true);
    }

    private BasePopupWindow(Activity mContext, boolean inflateImmediately, boolean shouldBackAlpha) {
        this(mContext, inflateImmediately, shouldBackAlpha, false);
    }

    /**
     * 构造
     *
     * @param mContext           mContext
     * @param inflateImmediately 是否在构造中就立即加载View，为false则需要在要加载视图时调用{@link #inflate()}方法
     * @param shouldBackAlpha    是否在该Window显示时调用{@link #backgroundAlpha(Activity, float)}使背景变暗，效果不太好
     * @param shouldMatchHeight  这个PopupWindow的高度是否强制占满全屏
     */
    public BasePopupWindow(Activity mContext, boolean inflateImmediately, boolean shouldBackAlpha, boolean shouldMatchHeight) {
        super(mContext);
        this.mContext = mContext;
        this.shouldBackAlpha = shouldBackAlpha;

        if (inflateImmediately) {
            inflate(shouldMatchHeight, true);
        }

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        backgroundAlpha(mContext, alpha);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        backgroundAlpha(mContext, alpha);
    }

    /**
     * 为该PopupWindow加载contentView
     *
     * @param context
     * @param shouldMatchHeight 是否将高设为全屏
     * @param shouldMatchWidth  是否将宽设为全屏
     */
    private void mInflate(Context context, boolean shouldMatchHeight, boolean shouldMatchWidth) {
        int layoutId = getLayoutId();
        View popupContentView = LayoutInflater.from(context).inflate(layoutId, null, false);
        setContentView(popupContentView);
        initView();

        if (shouldMatchWidth) {
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (shouldMatchHeight) {
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    protected abstract void initView();

    /**
     * 手动调用加载视图
     */
    protected void inflate() {
        inflate(false, true);
    }

    /**
     * 手动调用加载视图
     *
     * @param shouldMatchHeight
     * @param shouldMatchWidth
     */
    protected void inflate(boolean shouldMatchHeight, boolean shouldMatchWidth) {
        mInflate(mContext, shouldMatchHeight, shouldMatchWidth);
    }

    /**
     * 实现该方法，返回的View即是PopupWindow的contentView
     *
     * @return PopupWindow的contentView
     * @see #mInflate(Context, boolean, boolean)
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    public void backgroundAlpha(Activity context, float bgAlpha) {
        if (!shouldBackAlpha) {
            return;
        }
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(mContext, 1f);
    }

    public String getString(@StringRes int stringRes){
        return mContext.getString(stringRes);
    }
}
