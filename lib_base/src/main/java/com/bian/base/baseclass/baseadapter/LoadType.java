package com.bian.base.baseclass.baseadapter;


/**
 * @see OnDataLoadListener#onLoadStart(LoadType) 在该回调方法中用于区别数据加载类型
 * 依序对应下拉刷新，上拉加载更多，重新加载，初次加载
 * <p>
 * 注意该方法中的回调参数loadType是否等于{@link #FirstLoad}或{@link #Reload}取决于
 * 做首次加载数据或刷新(非下拉刷新，比如不同筛选条件下的数据刷新)时是否调用了{@link BasePTRAdapter#initLoad()}或{@link BasePTRAdapter#reloadData()}方法
 */
public enum  LoadType {
    Refresh,
    LoadMore,
    Reload,
    FirstLoad
}
