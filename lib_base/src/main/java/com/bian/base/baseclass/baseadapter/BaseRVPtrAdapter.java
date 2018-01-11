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
    private IPtrHandler ptrAdapterHandler;

    public BaseRVPtrAdapter(Context context) {
        super(context);
        init();
    }

    public BaseRVPtrAdapter(List<DataType> mData, Activity mActivity) {
        super(mData, mActivity);
        init();
    }

    private void init() {
        ptrAdapterHandler = new IPtrHandler<>(this);
    }

    @Override
    public void refreshUp() {
        ptrAdapterHandler.refreshUp();
    }

    @Override
    public void refreshDown() {
        ptrAdapterHandler.refreshDown();
    }

    @Override
    public void firstLoad() {
        ptrAdapterHandler.firstLoad();
    }

    @Override
    public void reload() {
        ptrAdapterHandler.reload();
    }

    @Override
    public int getPageNum() {
        return ptrAdapterHandler.getPageNum();
    }

    @Override
    public void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        ptrAdapterHandler.setOnDataLoadListener(onDataLoadListener);
    }

    @Override
    public void bindPtrLayout(PullToRefresh pullToRefresh, PtrMode mode) {
        ptrAdapterHandler.bindPtrLayout(pullToRefresh, mode);
    }

    @Override
    public void setDefaultPageSize(int defaultPageSize) {
        ptrAdapterHandler.setDefaultPageSize(defaultPageSize);
    }

    @Override
    public int getDataCount() {
        return getItemCount();
    }
}
