package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;


/**
 * author 边凌
 * date 2017/6/12 15:48
 * desc ${RecycleView的适配器基类}
 *
 * @see BasePtrAdapter 抽象思路同该类
 */

@SuppressWarnings("unused")
public abstract class BaseRVPtrAdapter<DataType, VH extends RecyclerView.ViewHolder>
        extends AbsRVAdapter<DataType, VH>
        implements IPtr, IPtr.Model<DataType> {
    private IPtrImpl iPtr;

    public BaseRVPtrAdapter(Context context) {
        super(context);
        init();
    }

    public BaseRVPtrAdapter(List<DataType> mData, Activity mActivity) {
        super(mData, mActivity);
        init();
    }

    private void init() {
        iPtr = new IPtrImpl<>(this);
    }

    @Override
    public final void refreshUp() {
        iPtr.refreshUp();
    }

    @Override
    public final void refreshDown() {
        iPtr.refreshDown();
    }

    @Override
    public final void firstLoad() {
        iPtr.firstLoad();
    }

    @Override
    public final void reload() {
        iPtr.reload();
    }

    @Override
    public final int getPageNum() {
        return iPtr.getPageNum();
    }

    @Override
    public final void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        iPtr.setOnDataLoadListener(onDataLoadListener);
    }

    @Override
    public final void bindPtrLayout(PullToRefresh pullToRefresh, PtrMode mode) {
        iPtr.bindPtrLayout(pullToRefresh, mode);
    }

    @Override
    public final void setDefaultPageSize(int defaultPageSize) {
        iPtr.setDefaultPageSize(defaultPageSize);
    }

    @Override
    public final int getDataCount() {
        return getItemCount();
    }
}
