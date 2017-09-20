package com.bian.base.baseclass.baseadapter;

import android.support.annotation.Nullable;

/**
 * 数据加载监听器，当该适配器被用于加载数据时，在加载开始，失败，成功三种状态回调该接口方法
 */
@SuppressWarnings("WeakerAccess")
public interface OnDataLoadListener {
    void onLoadSuccess();

    void onLoadFailed(int errorCode, @Nullable String msg);

    void onLoadStart(LoadType type);
}
