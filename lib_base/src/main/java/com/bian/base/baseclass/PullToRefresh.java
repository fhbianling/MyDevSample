package com.bian.base.baseclass;

/**
 * author 边凌
 * date 2017/4/24 11:04
 * desc ${对于可能会用到的上下拉刷新框架，实现该接口用于和{@link AbsBaseAdapter}配合实现上下拉}
 */

public interface PullToRefresh {
    void setMode(Mode mode);

    void onRefreshComplete();

    void setOnRefreshListener(OnRefreshListener onRefreshListener);

    enum Mode {Both, PullFromStart, PullFromEnd}

    interface OnRefreshListener {
        void onRefreshUp();

        void onRefreshDown();
    }
}
