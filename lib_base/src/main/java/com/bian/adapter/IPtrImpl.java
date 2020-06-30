package com.bian.adapter;

import androidx.annotation.Nullable;

import com.bian.util.core.L;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2018/1/11 16:43
 * 类描述：{@link IPtr}的真正实现类
 */
class IPtrImpl<T extends IPtr & IPtr.Model<DataType>, DataType>
        implements IPtr {
    //初始页页号，考虑到不同后端人员风格不一样，故抽为常量以便于更改，这个值有的后台会用1，一般为0
    private int INITIAL_PAGE_NUM = 0;
    private PullToRefresh ptr;
    private int dataPageSize = 10;
    private int pageSize = dataPageSize;
    private T adapter;
    private InnerDataHandler innerDataHandler;
    private int pageNum = INITIAL_PAGE_NUM;

    IPtrImpl(T adapter) {
        this.adapter = adapter;
        innerDataHandler = new InnerDataHandler(adapter.getDataLoader());
    }

    @Override
    public void refreshUp() {
        pageNum++;
        loadData(LoadType.LoadMore);
    }

    @Override
    public void refreshDown() {
        pageNum = INITIAL_PAGE_NUM;
        pageSize = Math.max(dataPageSize, adapter.getDataCount());
        loadData(LoadType.Refresh);
    }

    @Override
    public void firstLoad() {
        pageNum = INITIAL_PAGE_NUM;
        pageSize = dataPageSize;
        loadData(LoadType.FirstLoad);
    }

    @Override
    public void reload() {
        pageNum = INITIAL_PAGE_NUM;
        pageSize = Math.max(dataPageSize, adapter.getDataCount());
        loadData(LoadType.Reload);
    }

    @Override
    public int getPageNum() {
        return pageNum;
    }

    @Override
    public void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        innerDataHandler.setOnDataLoadListener(onDataLoadListener);
    }

    @Override
    public void bindPtrLayout(PullToRefresh pullToRefresh, final PtrMode ptrMode) {
        if (pullToRefresh != null) {
            ptr = pullToRefresh;
            ptr.setPtrMode(ptrMode);
            ptr.setOnRefreshListener(new IPtr.OnRefreshListener() {
                @Override
                public void onRefreshUp() {
                    if (ptrMode == IPtr.PtrMode.Both || ptrMode == IPtr.PtrMode.PullFromEnd) {
                        refreshUp();
                    }
                }

                @Override
                public void onRefreshDown() {
                    if (ptrMode == IPtr.PtrMode.Both || ptrMode == IPtr.PtrMode.PullFromStart) {
                        refreshDown();
                    }
                }
            });
        }
    }

    @Override
    public void setDefaultPageSize(int defaultSize) {
        this.dataPageSize = defaultSize;
    }

    void setInitialPageNum(int initialPageNum) {
        INITIAL_PAGE_NUM = initialPageNum;
    }

    /**
     * 从网络获取数据
     *
     * @param loadType {@link IPtr.LoadType}
     */
    private void loadData(IPtr.LoadType loadType) {
        if (innerDataHandler.isLoading()) return;
        innerDataHandler.loadData(pageNum, pageSize, loadType);
    }

    /**
     * 该内部类同时处理了{@link IPtr.DataLoader}和{@link IPtr.DataSetter}的职能
     */
    private class InnerDataHandler implements DataLoader<DataType>, DataSetter<DataType> {
        private DataLoader<DataType> dataLoader;
        private boolean isLoading;
        private OnDataLoadListener onDataLoadListener;
        private int pageNum;
        private LoadType loadType;

        InnerDataHandler(DataLoader<DataType> dataTypeDataLoader) {
            dataLoader = dataTypeDataLoader;
            if (dataLoader == null) {
                throw new UnsupportedOperationException(
                        "请调用setDataLoader(DataLoader)为AbsBaseAdapter设置数据加载器");
            }
        }

        @Override
        public void loadData(int pageNum, int pageSize, DataSetter<DataType> setter,
                             LoadType loadType) {
            setLoading(true);
            dataLoader.loadData(pageNum, pageSize, setter, loadType);
        }

        @Override
        public void setLoadedData(List<DataType> data) {
            setLoading(false);

            if (data == null || data.size() == 0) {
                loadSuccessButDataIsEmpty();
            } else {
                loadSuccess(data);
            }

            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadSuccess(loadType, pageNum);
            }
            if (ptr != null) {
                ptr.onRefreshComplete();
            }
        }

        @Override
        public void setFailed(int errorCode, @Nullable String dataErrorMsg) {
            setLoading(false);
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadFailed(loadType, errorCode, dataErrorMsg);
            }
        }

        boolean isLoading() {
            return isLoading;
        }

        void setLoading(boolean loading) {
            isLoading = loading;
        }

        void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
            this.onDataLoadListener = onDataLoadListener;
        }

        private void loadData(int pageNum, int pageSize, LoadType loadType) {
            this.pageNum = pageNum;
            this.loadType = loadType;
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadStart(loadType);
            }
            dataLoader.loadData(pageNum, pageSize, this, loadType);
        }

        private void loadSuccessButDataIsEmpty() {
            L.v("load success but data is empty");
            //当接口加载数据为空时，充值页面页号为前一页，若请求的是第一页数据，则重置为初始页页号
            pageNum = pageNum - 1;
            pageNum = Math.max(INITIAL_PAGE_NUM, pageNum);
            if (loadType == LoadType.Refresh || loadType == LoadType.Reload) {
                //如果当前加载类型是刷新或者重载，即意味着第一页就没数据，需要resetData以刷新UI
                adapter.resetData(new ArrayList<DataType>());
            }
        }

        private void loadSuccess(List<DataType> dataTypes) {
            if (pageNum == INITIAL_PAGE_NUM) {
                adapter.resetData(dataTypes);
            } else if (pageNum > INITIAL_PAGE_NUM) {
                adapter.addData(dataTypes);
            }
        }
    }

}
