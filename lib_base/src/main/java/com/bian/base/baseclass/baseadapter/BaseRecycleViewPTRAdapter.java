package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.bian.base.util.utilbase.L;

import java.util.ArrayList;
import java.util.List;

import static com.bian.base.baseclass.baseadapter.LoadType.FirstLoad;
import static com.bian.base.baseclass.baseadapter.LoadType.LoadMore;
import static com.bian.base.baseclass.baseadapter.LoadType.Refresh;
import static com.bian.base.baseclass.baseadapter.LoadType.Reload;


/**
 * author 边凌
 * date 2017/6/12 15:48
 * desc ${RecycleView的适配器基类}
 *
 * @see BasePTRAdapter 抽象思路同该类
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseRecycleViewPTRAdapter<DataType, VH extends RecyclerView.ViewHolder>
        extends AbsBaseRecycleViewAdapter<DataType, VH> {
    public static final int NO_ERROR_MSG = -1;
    private int defaultPageSize = 10;
    private int pageNum = 1;
    private int pageSize = defaultPageSize;
    private PullToRefresh pTr;
    private OnDataLoadListener onDataLoadListener;
    private DataSetter<DataType> innerDataSetter;
    private InnerDataLoader innerDataLoader;

    public BaseRecycleViewPTRAdapter(Activity mActivity) {
        super(mActivity);
    }

    public BaseRecycleViewPTRAdapter(List<DataType> mData, Activity mActivity) {
        super(mData, mActivity);
    }

    public BaseRecycleViewPTRAdapter(Activity mActivity, boolean loadData) {
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


    private void innerDataLoaderInit() {
        if (innerDataLoader == null) {
            innerDataLoader = new InnerDataLoader();
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

    /**
     * 下拉刷新
     */
    public final void refreshDown() {
        pageNum = 1;
        pageSize = Math.max(defaultPageSize, getItemCount());
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
     * 并且{@link #pageSize}固定为defaultPageSize
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
        pageSize = Math.max(defaultPageSize, getItemCount());
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
     * 根据传入的该类的Mode设置Ptr的mode
     *
     * @param mode {@link PullToRefresh.Mode}
     */
    private void setPtrMode(PullToRefresh.Mode mode) {
        switch (mode) {
            case Both:
                pTr.setMode(PullToRefresh.Mode.Both);
                break;
            case PullFromStart:
                pTr.setMode(PullToRefresh.Mode.PullFromStart);
                break;
            case PullFromEnd:
                pTr.setMode(PullToRefresh.Mode.PullFromEnd);
                break;
        }
    }

    public void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        this.onDataLoadListener = onDataLoadListener;
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
                mData = new ArrayList<>();
                notifyDataSetChanged();
            }
        }

        private void loadSuccess(List<DataType> dataTypes) {
            if (mData == null) {
                mData = new ArrayList<>();
            }

            if (pageNum == 1) {
                mData = dataAssignment(dataTypes);
            } else if (pageNum > 1) {
                List<DataType> dataTypes1 = dataAssignment(dataTypes);
                if (dataTypes1 != null) {
                    mData.addAll(dataTypes1);
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
}
