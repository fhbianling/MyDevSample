package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.bian.base.util.utilbase.L;

import java.util.ArrayList;
import java.util.List;

import static com.bian.base.baseclass.baseadapter.LoadType.FirstLoad;
import static com.bian.base.baseclass.baseadapter.LoadType.LoadMore;
import static com.bian.base.baseclass.baseadapter.LoadType.Refresh;
import static com.bian.base.baseclass.baseadapter.LoadType.Reload;
import static com.bian.base.baseclass.baseadapter.PullToRefresh.Mode.Both;


/**
 * 所有项目通用的ListView基类适配器
 * Created by fhbianling on 2016/10/30.
 * <p>
 * 需要填入泛型：用于展示的集合的泛型，请求数据返回的类型，ViewHolder
 * <p>
 * 包含上下拉刷新和数据请求的处理
 * 对于上下拉刷新，需要使用的上下拉刷新控件实现{@link PullToRefresh}接口，
 * 然后调用{@link #bindToPullToRefreshLayout(PullToRefresh, PullToRefresh.Mode)}方法绑定上下拉刷新控件
 * <p>
 * 该类的使用需要Retrofit2以及ButterKnife库的支持
 *
 * @see #setOnDataLoadListener(OnDataLoadListener) 对该适配器加载数据添加数据加载过程的监听支持
 * @see #setOnDataNotifyListener(OnDataNotifyListener) 对该适配器加载数据添加数据更新的监听支持
 */
// UPDATE: 2017/5/26 去掉对泛型E的类型限定，并将其改名为HolderClass,以支持dataBinding
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BasePTRAdapter<DataType, HolderClass extends AbsBaseAdapter.BaseHolder>
        extends AbsBaseAdapter<DataType, HolderClass> {
    public static final int NO_ERROR_MSG = -1;
    private int defaultPageSize = 10;

    private int pageNum = 1;
    private int pageSize = defaultPageSize;

    private PullToRefresh pTr;
    private OnDataLoadListener onDataLoadListener;
    private OnDataNotifyListener onDataNotifyListener;
    private DataSetter<DataType> innerDataSetter;
    private InnerDataLoader innerDataLoader;

    public BasePTRAdapter(Activity mActivity, boolean loadData) {
        super(mActivity);
        if (loadData) {
            innerDataLoaderInit();
            initLoad();
        }
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    protected DataLoader<DataType> getDataLoader() {
        return null;
    }

    /**
     * data改动，重写这个方法，可以对被赋值的data做出改动，可被重写。
     * {@link #addData(Object)},{@link #addData(List)},{@link #resetData(List)},
     * {@link #resetData(List)}四个方法
     * 以及该类自行调用{@link #loadData(LoadType)} 请求的数据，都会通过该方法进行数据转换
     * <p>
     *
     * @see InnerDataSetter
     * 默认实现不做任何转换
     */
    protected
    @Nullable
    List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    protected DataType dataAssignment(DataType dataType) {
        return dataType;
    }

    /**
     * 下拉刷新
     */
    public final void refreshDown() {
        pageNum = 1;
        pageSize = Math.max(defaultPageSize, getCount());
        loadData(Refresh);
    }

    /**
     * 上拉加载更多
     */
    public final void refreshUp() {
        pageNum++;
        loadData(LoadMore);
    }

    /**
     * 首次加载数据
     * <p>
     * 注意,调用该方法和{@link #refreshDown()}的唯一区别是
     * 会在{@link OnDataLoadListener#onLoadStart(LoadType)} 中传入不同参数用于区别数据的加载时机
     * <p>
     * 并且{@link #pageSize}固定为10
     */
    public final void initLoad() {
        pageNum = 1;
        pageSize = defaultPageSize;
        loadData(FirstLoad);
    }

    /**
     * 重新加载数据
     * <p>
     * 注意,调用该方法和{@link #refreshDown()}的唯一区别是
     * 会在{@link OnDataLoadListener#onLoadStart(LoadType)} 中传入不同参数用于区别数据的加载时机
     */
    public final void reloadData() {
        pageNum = 1;
        pageSize = Math.max(defaultPageSize, getCount());
        loadData(Reload);
    }

    /**
     * 绑定上下拉刷新库，为上下拉刷新控件添加监听
     *
     * @param pullToRefreshBase 实现了{@link PullToRefresh}接口的上下拉框架
     * @param mode              {@link PullToRefresh.Mode}
     */
    public final void bindToPullToRefreshLayout(PullToRefresh pullToRefreshBase, final PullToRefresh.Mode mode) {
        if (pullToRefreshBase != null) {
            pTr = pullToRefreshBase;
            setPtrMode(mode);
            pullToRefreshBase.setOnRefreshListener(new PullToRefresh.OnRefreshListener() {
                @Override
                public void onRefreshUp() {
                    if (mode == PullToRefresh.Mode.Both || mode == PullToRefresh.Mode.PullFromEnd) {
                        refreshUp();
                    }
                }

                @Override
                public void onRefreshDown() {
                    if (mode == PullToRefresh.Mode.Both || mode == PullToRefresh.Mode.PullFromStart) {
                        refreshDown();
                    }
                }
            });
        }
    }

    /**
     * 设置数据加载监听器，当数据开始加载，加载成功或加载失败时被回调
     *
     * @param onDataLoadListener {@link OnDataLoadListener}
     */
    public final void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        this.onDataLoadListener = onDataLoadListener;
    }

    /**
     * 根据传入的该类的Mode设置Ptr的mode
     *
     * @param mode {@link PullToRefresh.Mode}
     */
    private void setPtrMode(PullToRefresh.Mode mode) {
        switch (mode) {
            case Both:
                pTr.setMode(Both);
                break;
            case PullFromStart:
                pTr.setMode(PullToRefresh.Mode.PullFromStart);
                break;
            case PullFromEnd:
                pTr.setMode(PullToRefresh.Mode.PullFromEnd);
                break;
        }
    }

    /**
     * 从网络获取数据
     *
     * @param loadType {@link LoadType}
     */
    private void loadData(LoadType loadType) {
        innerDataLoaderInit();
        if (onDataLoadListener != null) {
            onDataLoadListener.onLoadStart(loadType);
        }
        if (innerDataSetter == null) {
            innerDataSetter = new InnerDataSetter();
        }
        if (innerDataLoader.isLoading()) return;
        ((InnerDataSetter) innerDataSetter).setLoadType(loadType);
        innerDataLoader.loadData(pageNum, pageSize, innerDataSetter, loadType);
    }

    private void innerDataLoaderInit() {
        if (innerDataLoader == null) {
            innerDataLoader = new InnerDataLoader();
        }
    }

    @CallSuper
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (onDataNotifyListener != null) {
            onDataNotifyListener.onNotifyDataSetChanged();
        }
    }

    @CallSuper
    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        if (onDataNotifyListener != null) {
            onDataNotifyListener.onNotifyDataSetInvalidated();
        }
    }

    private final class InnerDataLoader implements DataLoader<DataType> {
        private DataLoader<DataType> dataLoader;
        private boolean isLoading;

        InnerDataLoader() {
            dataLoader = getDataLoader();
            if (dataLoader == null) {
                throw new UnsupportedOperationException(
                        "请调用setDataLoader(DataLoader)为AbsBaseAdapter设置数据加载器");
            }
        }

        boolean isLoading() {
            return isLoading;
        }

        void setLoading(boolean loading) {
            isLoading = loading;
        }

        @Override
        public void loadData(int pageNum, int pageSize, DataSetter<DataType> setter,
                             LoadType loadType) {
            isLoading = true;
            dataLoader.loadData(pageNum, pageSize, setter, loadType);
        }
    }

    private final class InnerDataSetter implements DataSetter<DataType> {
        private LoadType loadType;

        void setLoadType(LoadType loadType) {
            this.loadType = loadType;
        }

        @Override
        public void setLoadedData(List<DataType> data) {
            innerDataLoader.setLoading(false);
            if (data == null || data.size() == 0) {
                loadSuccessButDataIsEmpty();
                listenerActionSuccess();
                return;
            }
            loadSuccess(data);
            listenerActionSuccess();
            notifyDataSetChanged();
        }

        @Override
        public void setFailed(String dataError) {
            innerDataLoader.setLoading(false);
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadFailed(NO_ERROR_MSG, dataError);
            }
        }

        @Override
        public void setFailed(int errorCode, @Nullable String dataErrorMsg) {
            innerDataLoader.setLoading(false);
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadFailed(errorCode, dataErrorMsg);
            }
        }

        private void listenerActionSuccess() {
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadSuccess();
            }
        }

        private void loadSuccessButDataIsEmpty() {
            L.d("loadSuccessButDataIsEmpty");
            resetPageAndEndRefresh();
            if (loadType == Refresh || loadType == Reload) {
                setData(new ArrayList<DataType>());
                notifyDataSetChanged();
            }
        }

        private void loadSuccess(List<DataType> dataTypes) {
            if (getData() == null) {
                setData(new ArrayList<DataType>());
            }

            if (pageNum == 1) {
                setData(dataAssignment(dataTypes));
            } else if (pageNum > 1) {
                List<DataType> dataTypes1 = dataAssignment(dataTypes);
                if (dataTypes1 != null) {
                    List<DataType> data = getData();
                    if (data != null) {
                        data.addAll(dataTypes1);
                        setData(data);
                    }
                }
            }

            if (pTr != null) {
                pTr.onRefreshComplete();
            }
        }

        private void resetPageAndEndRefresh() {
            pageNum = pageNum - 1;
            pageNum = Math.max(1, pageNum);
            if (pTr != null) {
                pTr.onRefreshComplete();
            }
        }
    }

}
