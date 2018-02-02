package com.bian.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.util.eventbus.BusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * author 边凌
 * date 2017/4/21 10:31
 * desc ${Fragment基类}
 * 功能：
 * 事件总线支持
 * {@link #shouldRegisterBus()}返回true，则该Fragment会注册事件总线，否则不会。
 * 当注册后，重写{@link #handleMessage(Object)}即可接受事件
 */

public abstract class AbsBaseFragment extends Fragment {
    private boolean first = true;
    private boolean firstOnResume = true;
    private View mView;

    /**
     * 初始化布局
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);

    /**
     * 初始化view
     */
    protected abstract void initView(View rootView);

    protected boolean shouldRegisterBus() {
        return false;
    }

    /**
     * 第一次对用户可见的回调
     */
    protected void firstVisibleToUser() {

    }

    protected void onResume(boolean isFirst) {

    }

    @Override
    public void onResume() {
        super.onResume();
        onResume(firstOnResume);
        firstOnResume = false;
    }

    /**
     * 处理事件总线事件
     *
     * @param msg 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(Object msg) {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mView = createView(inflater, container, savedInstanceState);
        initView(mView);
        return mView;
    }

    private void registerBus() {
        if (shouldRegisterBus()) {
            BusUtil.get().register(this);
        }
    }

    @CallSuper
    public void onStart() {
        super.onStart();
        registerBus();
    }

    @CallSuper
    public void onStop() {
        super.onStop();
        unregisterBus();
    }

    private void unregisterBus() {
        if (shouldRegisterBus()) {
            BusUtil.get().unregister(this);
        }
    }

    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && first) {
            firstVisibleToUser();
            first = false;
        }
    }

    public
    @Nullable
    <T extends View> T findViewById(@IdRes int id) {
        return mView != null ? (T) mView.findViewById(id) : null;
    }
}
