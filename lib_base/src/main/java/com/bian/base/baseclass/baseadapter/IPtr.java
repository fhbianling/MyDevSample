package com.bian.base.baseclass.baseadapter;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * author 边凌
 * date 2018/1/11 15:08
 * 类描述：
 */

public interface IPtr {
    void refreshUp();

    void refreshDown();

    void firstLoad();

    void reload();

    int getPageNum();

    void setOnDataLoadListener(OnDataLoadListener onDataLoadListener);

    void bindPtrLayout(PullToRefresh pullToRefresh, PtrMode mode);

    void setDefaultPageSize(int defaultSize);

    /**
     * @see OnDataLoadListener#onLoadStart(LoadType) 在该回调方法中用于区别数据加载类型
     * 依序对应下拉刷新，上拉加载更多，重新加载，初次加载
     * <p>
     * 注意该方法中的回调参数loadType是否等于{@link #FirstLoad}或{@link #Reload}取决于
     * 做首次加载数据或刷新(非下拉刷新，比如不同筛选条件下的数据刷新)时
     * 调用的是{@link IPtr#firstLoad()} 还是{@link IPtr#reload()} 方法
     */
    enum LoadType {
        Refresh,
        LoadMore,
        Reload,
        FirstLoad
    }

    enum PtrMode {Both, PullFromStart, PullFromEnd}

    interface Model<DataType> {
        DataLoader<DataType> getDataLoader();

        int getDataCount();

        void resetData(List<DataType> data);

        void addData(List<DataType> data);
    }

    /**
     * 数据加载监听器，当该适配器被用于加载数据时，在加载开始，失败，成功三种状态回调该接口方法
     */
    interface OnDataLoadListener {
        void onLoadSuccess();

        void onLoadFailed(int errorCode, @Nullable String msg);

        void onLoadStart(LoadType type);
    }

    /**
     * author 边凌
     * date 2017/4/24 11:04
     * desc ${对于可能会用到的上下拉刷新框架，实现该接口用于和{@link IPtr}的子类配合实现上下拉刷新数据}
     */
    interface PullToRefresh {
        void setPtrMode(PtrMode mode);

        void onRefreshComplete();

        void setOnRefreshListener(OnRefreshListener onRefreshListener);

    }

    /**
     * 该监听器需要{@link PullToRefresh}来实现
     */
    interface OnRefreshListener {
        void onRefreshUp();

        void onRefreshDown();
    }

    /**
     * author 边凌
     * date 2017/9/20 9:54
     * 类描述：该类用于在数据加载后进行数据设置，
     * 这个引用会在{@link DataLoader#loadData(int, int, DataSetter, LoadType)}这个重写方法
     * 被调用时由{@link IPtr}内部创建并传入
     */
    interface DataSetter<T> {

        void setLoadedData(List<T> data);

        void setFailed(int errorCode, @Nullable String dataErrorMsg);
    }

    /**
     * author 边凌
     * date 2017/9/20 9:54
     * 类描述：数据加载器，在方法{@link #loadData(int, int, DataSetter, LoadType)}中加载数据，
     * 加载成功后,方法参数{@link DataSetter}是一个Adapter的内部实现类对象的引用，不应对这个setter引用作任何重新赋值的操作
     * <p>
     * 调用其方法{@link DataSetter#setLoadedData(List)}或{@link DataSetter#setFailed(int, String)}设置数据
     */
    interface DataLoader<DataType> {
        void loadData(int pageNum, int pageSize, final DataSetter<DataType> setter,
                      LoadType loadType);
    }
}
