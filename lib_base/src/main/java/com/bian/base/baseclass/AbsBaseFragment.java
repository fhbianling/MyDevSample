package com.bian.base.baseclass;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.base.util.utilevent.EventUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * author 边凌
 * date 2017/4/21 10:31
 * desc ${Fragment基类}
 */

@SuppressWarnings({"UnusedParameters", "unused"})
public abstract class AbsBaseFragment extends Fragment {
    private boolean first = true;
    private boolean firstOnResume = true;

    /**
     * 初始化布局
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 初始化view
     */
    protected abstract void initView(View rootView);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = createView(inflater, container, savedInstanceState);
        initView(mView);
        return mView;
    }

    private void registerRxBus() {
        EventUtil.get().register(this);
    }

    @CallSuper
    public void onStart() {
        super.onStart();
        registerRxBus();
    }

    @CallSuper
    public void onStop() {
        super.onStop();
        unregisterRxBus();
    }

    private void unregisterRxBus() {
        EventUtil.get().unregister(this);
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
    <T extends View> View findViewById(@IdRes int id) {
        return getView() != null ? getView().findViewById(id) : null;
    }
}
