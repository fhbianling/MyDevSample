package com.bian.base.baseclass.baseadapter;

import android.support.annotation.Nullable;

import com.bian.base.util.utilbase.L;

import java.util.ArrayList;
import java.util.List;

import static com.bian.base.baseclass.baseadapter.IPtr.LoadType.FirstLoad;
import static com.bian.base.baseclass.baseadapter.IPtr.LoadType.LoadMore;
import static com.bian.base.baseclass.baseadapter.IPtr.LoadType.Refresh;
import static com.bian.base.baseclass.baseadapter.IPtr.LoadType.Reload;

/**
 * author 边凌
 * date 2018/1/11 16:43
 * 类描述：{@link IPtr}的真正实现类
 */
class IPtrImpl<T extends IPtr & IPtr.Model<DataType>, DataType>
        implements IPtr {

    private PullToRefresh ptr;
    private int dataPageSize = 10;
    private int pageSize = dataPageSize;
    private T adapter;
    private InnerDataLoader innerDataLoader;
    private int pageNum = 1;

    IPtrImpl(T adapter) {
        this.adapter = adapter;
        innerDataLoader = new InnerDataLoader(adapter.getDataLoader());
    }

    /**
     * 从网络获取数据
     *
     * @param loadType {@link IPtr.LoadType}
     */
    private void loadData(IPtr.LoadType loadType) {
        if (innerDataLoader.isLoading()) return;
        innerDataLoader.loadData(pageNum, pageSize, loadType);
    }


    @Override
    public void refreshUp() {
        pageNum++;
        loadData(LoadMore);
    }

    @Override
    public void refreshDown() {
        pageNum = 1;
        pageSize = Math.max(dataPageSize, adapter.getDataCount());
        loadData(Refresh);
    }

    @Override
    public void firstLoad() {
        pageNum = 1;
        pageSize = dataPageSize;
        loadData(FirstLoad);
    }

    @Override
    public void reload() {
        pageNum = 1;
        pageSize = Math.max(dataPageSize, adapter.getDataCount());
        loadData(Reload);
    }

    @Override
    public int getPageNum() {
        return pageNum;
    }

    @Override
    public void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
        innerDataLoader.setOnDataLoadListener(onDataLoadListener);
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

    private class InnerDataLoader implements DataLoader<DataType>, DataSetter<DataType> {
        private DataLoader<DataType> dataLoader;
        private boolean isLoading;
        private OnDataLoadListener onDataLoadListener;
        private LoadType loadType;

        InnerDataLoader(DataLoader<DataType> dataTypeDataLoader) {
            dataLoader = dataTypeDataLoader;
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
            setLoading(true);
            dataLoader.loadData(pageNum, pageSize, setter, loadType);
        }

        private void loadData(int pageNum, int pageSize, LoadType loadType) {
            this.loadType = loadType;
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadStart(loadType);
            }
            dataLoader.loadData(pageNum, pageSize, this, loadType);
        }

        void setOnDataLoadListener(OnDataLoadListener onDataLoadListener) {
            this.onDataLoadListener = onDataLoadListener;
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
                onDataLoadListener.onLoadSuccess();
            }
            if (ptr != null) {
                ptr.onRefreshComplete();
            }
        }

        private void loadSuccessButDataIsEmpty() {
            L.v("load success but data is empty");
            pageNum = pageNum - 1;
            pageNum = Math.max(1, pageNum);
            if (loadType == Refresh || loadType == Reload) {
                adapter.resetData(new ArrayList<DataType>());
            }
        }

        @Override
        public void setFailed(int errorCode, @Nullable String dataErrorMsg) {
            setLoading(false);
            if (onDataLoadListener != null) {
                onDataLoadListener.onLoadFailed(errorCode, dataErrorMsg);
            }
        }

        private void loadSuccess(List<DataType> dataTypes) {
            if (pageNum == 1) {
                adapter.resetData(dataTypes);
            } else if (pageNum > 1) {
                adapter.addData(dataTypes);
            }
        }
    }

}
