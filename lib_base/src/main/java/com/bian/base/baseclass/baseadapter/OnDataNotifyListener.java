package com.bian.base.baseclass.baseadapter;

import android.database.DataSetObserver;

/**
 * 数据更新监听器，
 * 注意，这个监听器只在{@link BasePTRAdapter#notifyDataSetChanged()}或{@link BasePTRAdapter#notifyDataSetInvalidated()}
 * 方法被调用时会回调，并没有通过{@link BasePTRAdapter#registerDataSetObserver(DataSetObserver)}来回调，
 * 换句话说，它只能监听上述两个方法是否被调用
 */
public interface OnDataNotifyListener {
    void onNotifyDataSetChanged();

    void onNotifyDataSetInvalidated();
}
