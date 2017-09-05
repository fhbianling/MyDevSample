package com.bian.base.baseclass;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.bian.base.util.utilbase.L;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 所有项目通用的ListView基类适配器
 * Created by fhbianling on 2016/10/30.
 * <p>
 * 需要填入泛型：用于展示的集合的泛型，请求数据返回的类型，ViewHolder
 * <p>
 * 包含上下拉刷新和数据请求的处理
 * 对于上下拉刷新，需要使用的上下拉刷新控件实现{@link PullToRefresh}接口，
 * 然后调用{@link #bindToPullToRefreshLayout(PullToRefresh, Mode)}方法绑定上下拉刷新控件
 * <p>
 * 该类的使用需要Retrofit2以及ButterKnife库的支持
 *
 * @see #setOnDataLoadListener(OnDataLoadListener) 对该适配器加载数据添加数据加载过程的监听支持
 * @see #setOnDataNotifyListener(OnDataNotifyListener) 对该适配器加载数据添加数据更新的监听支持
 */
// UPDATE: 2017/5/26 去掉对泛型E的类型限定，并将其改名为HolderClass,以支持dataBinding
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class AbsBaseAdapter<DataType, CallType, HolderClass> extends android.widget.BaseAdapter {
    public final static int Refresh = 0x13;
    public final static int LoadMore = 0x14;
    public final static int Reload = 0x15;
    public final static int FirstLoad = 0x16;
    protected Activity mActivity;
    protected LayoutInflater inflater;
    private List<DataType> mData;

    private int pageNum = 1;
    private int pageSize = 10;

    private PullToRefresh pTr;
    private OnDataLoadListener<CallType> onDataLoadListener;
    private OnDataNotifyListener onDataNotifyListener;

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
            Call<CallType> testCall = getCall(1, 1);
            if (testCall == null) {
                throw new NullPointerException("需要重写Call<CallType> getCall(int pageNum, int pageSize)方法以请求数据");
            }
            refreshDown();
        }
    }

    /**
     * 获得Holder
     */
    protected abstract
    @NonNull
    HolderClass getHolder(View convertView);

    public Activity getActivity(){
        return mActivity;
    }

    /**
     * 展示数据
     */
    protected abstract void displayData(int position, int viewType, @NonNull HolderClass holder, @NonNull DataType dataType, boolean isLast);

    /**
     * 获得View
     */
    protected abstract View getViewByType(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 返回一个用于请求数据的{@link Call}
     *
     * @see #convertData(Object)
     */
    public Call<CallType> getCall(int pageIndex, int pageSize) {
        return null;
    }

    /**
     * 将Call获得的数据类型转换为需要展示的数据类型的集合
     *
     * @see #getCall(int, int)
     */
    public List<DataType> convertData(CallType callData) {
        return null;
    }

    /**
     * data改动，重写这个方法，可以对被赋值的data做出改动，可被重写。
     * {@link #addData(Object)},{@link #addData(List)},{@link #resetData(List)},{@link #resetData(List)}四个方法
     * 以及该类自行调用{@link Call}请求的数据，都会通过该方法进行数据转换
     * <p>
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
        pageSize = Math.max(10, getCount());
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
     * 会在{@link OnDataLoadListener#onLoadStart(int)}中传入不同参数用于区别数据的加载时机
     * <p>
     * 并且{@link #pageSize}固定为10
     */
    public final void initLoad() {
        pageNum = 1;
        pageSize = 10;
        loadData(FirstLoad);
    }

    /**
     * 重新加载数据
     * <p>
     * 注意,调用该方法和{@link #refreshDown()}的唯一区别是
     * 会在{@link OnDataLoadListener#onLoadStart(int)}中传入不同参数用于区别数据的加载时机
     */
    public final void reloadData() {
        pageNum = 1;
        pageSize = Math.max(10, getCount());
        loadData(Reload);
    }

    /**
     * 绑定上下拉刷新库，为上下拉刷新控件添加监听
     *
     * @param pullToRefreshBase 实现了{@link PullToRefresh}接口的上下拉框架
     * @param mode              {@link Mode}
     */
    public final void bindToPullToRefreshLayout(PullToRefresh pullToRefreshBase, final Mode mode) {
        if (pullToRefreshBase != null) {
            pTr = pullToRefreshBase;
            setPtrMode(mode);
            pullToRefreshBase.setOnRefreshListener(new PullToRefresh.OnRefreshListener() {
                @Override
                public void onRefreshUp() {
                    if (mode == Mode.Both || mode == Mode.OnlyPullUp) {
                        refreshUp();
                    }
                }

                @Override
                public void onRefreshDown() {
                    if (mode == Mode.Both || mode == Mode.OnlyPullDown) {
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
    public final void setOnDataLoadListener(OnDataLoadListener<CallType> onDataLoadListener) {
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
     * @param mode {@link Mode}
     */
    private void setPtrMode(Mode mode) {
        switch (mode) {
            case Both:
                pTr.setMode(PullToRefresh.Mode.Both);
                break;
            case OnlyPullDown:
                pTr.setMode(PullToRefresh.Mode.PullFromStart);
                break;
            case OnlyPullUp:
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
    private void loadData(final @LoadType int loadType) {
        Call<CallType> dataCall = getCall(pageNum, pageSize);
        if (dataCall == null) {
            throw new UnsupportedOperationException("如果要调用绑定上下拉刷新加载操作，请重写getCall(int pageNum, int pageSize)方法");
        }
        if (onDataLoadListener != null) {
            onDataLoadListener.onLoadStart(loadType);
        }
        dataCall.enqueue(new Callback<CallType>() {
            @Override
            public void onResponse(Call<CallType> call, Response<CallType> response) {
                pageSize = 10;
                CallType body = response.body();
                if (body != null) {
                    /*注意下面这个代码块的顺序不能更改，某些情况下可能其子类会在convertData中做一些操作
                    /*同时在onDataLoadListener的onLoadSuccess回调中取数据，因此调换顺序可能会导致空指针
                    /*还可能在loadSuccess中getCount
                    /*---------------------------------------------------------------------*/
                    List<DataType> dataTypes = convertData(body);
                    if (dataTypes == null || dataTypes.size() == 0) {
                        loadSuccessButDataIsEmpty();
                        listenerActionSuccess();
                        return;
                    }
                    loadSuccess(dataTypes);
                    listenerActionSuccess();
                    notifyDataSetChanged();
                    /*---------------------------------------------------------------------*/
                } else {
                    if (onDataLoadListener != null) {
                        onDataLoadListener.onLoadFailed(response);
                    }
                    resetPageAndEndRefresh();

                }
            }

            @Override
            public void onFailure(Call<CallType> call, Throwable t) {
                pageSize = 10;
                resetPageAndEndRefresh();
                if (onDataLoadListener != null) {
                    onDataLoadListener.onLoadFailed(null);
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
        });
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
     * 当不需要上下拉刷新时，
     * 不调用{@link #bindToPullToRefreshLayout(PullToRefresh, Mode)}方法即可
     * 该枚举用于设置是否支持上拉，下拉，两者三种情况下的数据加载
     * <p>
     * 需要所用到的上下拉刷新框架实现{@link PullToRefresh}接口
     */
    @SuppressWarnings("WeakerAccess")
    public enum Mode {
        /*只有下拉刷新*/
        OnlyPullDown,
        /*只有上拉加载*/
        OnlyPullUp,
        /*两者都有*/
        Both
    }


    /**
     * 数据加载监听器，当该适配器被用于加载数据时，在加载开始，失败，成功三种状态回调该接口方法
     */
    @SuppressWarnings("WeakerAccess")
    public interface OnDataLoadListener<CallType> {
        void onLoadSuccess();

        void onLoadFailed(Response<CallType> response);

        void onLoadStart(@LoadType int type);
    }

    /**
     * author 边凌
     * date 2017/4/24 11:04
     * desc ${对于可能会用到的上下拉刷新框架，实现该接口用于和{@link AbsBaseAdapter}配合实现上下拉}
     */

    public interface PullToRefresh {
        void setMode(Mode mode);

        void onRefreshComplete();

        void setOnRefreshListener(OnRefreshListener onRefreshListener);

        enum Mode {Both, PullFromStart, PullFromEnd}

        interface OnRefreshListener {
            void onRefreshUp();

            void onRefreshDown();
        }
    }

    /**
     * 数据更新监听器，注意，这个监听器只在{@link #notifyDataSetChanged()}或{@link #notifyDataSetInvalidated()}
     * 方法被调用时会回调，并没有通过{@link #registerDataSetObserver(DataSetObserver)}来回调，
     * 换句话说，它只能监听上述两个方法是否被调用
     */
    public interface OnDataNotifyListener {
        void onNotifyDataSetChanged();

        void onNotifyDataSetInvalidated();
    }

    /**
     * @see OnDataLoadListener#onLoadStart(int) 在该回调方法中用于区别数据加载类型
     * 依序对应下拉刷新，上拉加载更多，重新加载，初次加载
     * <p>
     * 注意该方法中的回调参数loadType是否等于{@link #FirstLoad}或{@link #Reload}取决于
     * 做首次加载数据或刷新(非下拉刷新，比如不同筛选条件下的数据刷新)时是否调用了{@link #initLoad()}或{@link #reloadData()}方法
     */
    @IntDef({Refresh, LoadMore, Reload, FirstLoad})
    public @interface LoadType {
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

}
