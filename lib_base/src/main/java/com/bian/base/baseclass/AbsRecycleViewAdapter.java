package com.bian.base.baseclass;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.base.util.utilbase.L;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author 边凌
 * date 2017/6/12 15:48
 * desc ${RecycleView的适配器基类}
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbsRecycleViewAdapter<DataType, CallType, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public final static int Refresh = 0x13;
    public final static int LoadMore = 0x14;
    public final static int Reload = 0x15;
    public final static int FirstLoad = 0x16;
    private final static long INTERVAL = 300;
    private final static String LOG_TAG = "AbsRecycleViewAdapter";
    private final Activity mActivity;
    private final LayoutInflater inflater;
    private long lastClickTime;
    public List<DataType> mData = new ArrayList<>();
    private int pageNum = 1;
    private int pageSize = 10;
    private PullToRefresh pTr;
    private OnDataLoadListener<CallType> onDataLoadListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;
    public AbsRecycleViewAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
    }

    public AbsRecycleViewAdapter(List<DataType> mData, Activity mActivity) {
        this(mActivity);
        this.mData = dataAssignment(mData);
    }

    public AbsRecycleViewAdapter(Activity mActivity, boolean loadData) {
        this(mActivity);
        if (loadData) {
            Call<CallType> testCall = getCall(1, 1);
            if (testCall == null) {
                throw new NullPointerException("需要重写Call<CallType> getCall(int pageNum, int pageSize)方法以请求数据");
            }
            initLoad();
        }
    }

    public List<DataType> getData() {
        if (mData == null) {
            return null;
        } else {
            return new ArrayList<>(mData);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateHolder(inflater, parent, viewType);
    }

    protected abstract VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    @CallSuper
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastClick()) return;
                if (onItemClickListener != null) {
                    onItemClickEvent(holder);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickEvent(holder);
                }
                return true;
            }
        });
        bindView(holder, position, getItem(positionAssignment(position)));
    }

    protected int positionAssignment(int position){
        return position;
    }

    @CallSuper
    protected void onItemLongClickEvent(VH holder) {
        onItemLongClickListener.onItemLongClick(positionAssignment(holder.getAdapterPosition()));
    }

    @CallSuper
    protected void onItemClickEvent(VH holder) {
        onItemClickListener.onItemClick(positionAssignment(holder.getAdapterPosition()));
    }

    private boolean isFastClick() {
        boolean isFastClick = System.currentTimeMillis() - lastClickTime < INTERVAL;
        lastClickTime = System.currentTimeMillis();
        return isFastClick;
    }

    protected abstract void bindView(VH holder, int realPosition, DataType item);

    protected
    @Nullable
    List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    protected DataType dataAssignment(@Nullable DataType dataType) {
        return dataType;
    }

    public DataType getItem(int position) {
        return position > mData.size() - 1 ? null : mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

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
    public List<DataType> convertData(@NonNull CallType callData) {
        return null;
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
                Log.d(LOG_TAG, "loadDataOnFailure:" + (t != null ? t.getMessage() : ""));
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

    public void addData(DataType dataType) {
        DataType data = dataAssignment(dataType);
        mData.add(data);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeData(int position) {
        mData.remove(positionAssignment(position));
        notifyItemRemoved(positionAssignment(position));
    }

    public void resetData(List<DataType> dataTypes) {
        mData = dataAssignment(dataTypes);
        notifyDataSetChanged();
    }

    /**
     * 下拉刷新
     */
    public final void refreshDown() {
        pageNum = 1;
        pageSize = Math.max(10, getItemCount());
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
        pageSize = Math.max(10, getItemCount());
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnDataLoadListener(OnDataLoadListener<CallType> onDataLoadListener) {
        this.onDataLoadListener = onDataLoadListener;
    }

    public Activity getActivity() {
        return mActivity;
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
     * @see OnDataLoadListener#onLoadStart(int) 在该回调方法中用于区别数据加载类型
     * 依序对应下拉刷新，上拉加载更多，重新加载，初次加载
     * <p>
     * 注意该方法中的回调参数loadType是否等于{@link #FirstLoad}或{@link #Reload}取决于
     * 做首次加载数据或刷新(非下拉刷新，比如不同筛选条件下的数据刷新)时是否调用了{@link #initLoad()}或{@link #reloadData()}方法
     */
    @IntDef({Refresh, LoadMore, Reload, FirstLoad})
    public @interface LoadType {
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
