package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public abstract class AbsBaseAdapter<DataType, HolderClass>
        extends android.widget.BaseAdapter {
    public static final int NO_ERROR_MSG = -1;
    private int defaultPageSize = 10;
    protected Activity mActivity;
    protected LayoutInflater inflater;
    private List<DataType> mData;

    private int pageNum = 1;
    private int pageSize = defaultPageSize;

    private PullToRefresh pTr;
    private OnDataLoadListener onDataLoadListener;
    private OnDataNotifyListener onDataNotifyListener;
    private DataSetter<DataType> innerDataSetter;
    private InnerDataLoader innerDataLoader;

    public AbsBaseAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
    }

    public AbsBaseAdapter(List<DataType> mData, Activity mActivity) {
        this(mActivity);
        this.mData = dataAssignment(mData);
    }

    public AbsBaseAdapter(Activity mActivity, boolean loadData) {
        this(mActivity);
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
     * 获得Holder
     */
    protected abstract
    @NonNull
    HolderClass getHolder(View convertView);

    public Activity getActivity() {
        return mActivity;
    }

    /**
     * 展示数据
     */
    protected abstract void displayData(int position, int viewType, @NonNull HolderClass holder,
                                        @NonNull DataType dataType, boolean isLast);

    /**
     * 获得View
     */
    protected abstract View getViewByType(LayoutInflater inflater, ViewGroup parent, int viewType);

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
     * 重置数据
     *
     * @param data data
     */
    public final void resetData(List<DataType> data) {
        this.mData = dataAssignment(data);
        notifyDataSetChanged();
    }

    /**
     * 将指定位置的数据替换
     *
     * @param position position
     * @param dataType 展示的数据类型的实体
     */
    public final void replaceData(int position, DataType dataType) {
        if (mData == null) {
            throw new NullPointerException();
        }
        if (position > mData.size()) {
            throw new IllegalArgumentException();
        }
        mData.set(position, dataAssignment(dataType));
        notifyDataSetChanged();
    }

    /**
     * 添加集合数据
     *
     * @param data data
     */
    public final void addData(List<DataType> data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        List<DataType> dataTypes = dataAssignment(data);
        if (dataTypes != null) {
            mData.addAll(dataTypes);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加单个数据
     *
     * @param dataType 展示的数据类型
     */
    public final void addData(DataType dataType) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(dataAssignment(dataType));
        notifyDataSetChanged();
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
     * 获得当前数据集的保护性拷贝
     */
    public final
    @Nullable
    List<DataType> getData() {
        if (mData == null) {
            return null;
        }
        return new ArrayList<>(mData);
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

    @CallSuper
    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public final DataType getItem(int i) {
        return mData != null ? mData.get(i) : null;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        int viewType = getItemViewType(i);
        HolderClass holder;
        if (view == null) {
            view = getViewByType(inflater, viewGroup, viewType);
            holder = getHolder(view);
            view.setTag(holder);
        } else {
            /*注意不能对getView获得的根视图设置tag*/

            //noinspection unchecked
            holder = (HolderClass) view.getTag();
        }
        DataType item = getItem(i);
        if (item != null) {
            displayData(i, viewType, holder, item, i == getCount() - 1);
        }
        return view;
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

    public final void setOnDataNotifyListener(OnDataNotifyListener onDataNotifyListener) {
        this.onDataNotifyListener = onDataNotifyListener;
    }

    /**
     * 用于提供ListView缓存机制实现的基类ViewHolder，这里单独抽类是为了添加对ButterKnife的支持
     */
    public static abstract class BaseViewHolder {
        protected View root;

        public BaseViewHolder(View root) {
            this.root = root;
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
        public void setData(List<DataType> data) {
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

}