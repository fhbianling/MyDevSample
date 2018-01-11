package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.content.Context;

import java.util.List;


/**
 * 所有项目通用的ListView基类适配器
 * Created by fhbianling on 2016/10/30.
 * <p>
 * 需要填入泛型：用于展示的集合的泛型，请求数据返回的类型，ViewHolder
 * <p>
 * 包含上下拉刷新和数据请求的处理
 * 对于上下拉刷新，需要使用的上下拉刷新控件实现{@link IPtr.PullToRefresh}接口，
 * 然后调用{@link #bindPtrLayout(PullToRefresh, PtrMode)} 方法绑定上下拉刷新控件
 * <p>
 * 该类的使用需要Retrofit2以及ButterKnife库的支持
 * update:2017/9/30 现在不再仅限定于Retrofit2的网络框架，
 * 通过重写{@link #getDataLoader()}方法提供一个数据加载器，以适配任何一个网络框架,
 *
 * @see RetrofitDataLoader 一个实现了{@link DataLoader}接口的Retrofit数据加载器的示例
 * @see #setOnDataLoadListener(IPtr.OnDataLoadListener) 对该适配器加载数据添加数据加载过程的监听支持
 */
// UPDATE: 2017/5/26 去掉对泛型E的类型限定，并将其改名为HolderClass,以支持dataBinding
@SuppressWarnings("unused")
public abstract class BasePtrAdapter<DataType, HolderClass extends AbsAdapter.ViewHolder>
        extends AbsAdapter<DataType, HolderClass>
        implements IPtr, IPtr.Model<DataType> {

    private IPtrHandler ptrAdapterHandler;

    public BasePtrAdapter(Context context) {
        super(context);
        init();
    }

    public BasePtrAdapter(List<DataType> mData, Activity mContext) {
        super(mData, mContext);
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
    public void setDefaultPageSize(int defaultSize) {
        ptrAdapterHandler.setDefaultPageSize(defaultSize);
    }

    @Override
    public int getDataCount() {
        return getCount();
    }

}
