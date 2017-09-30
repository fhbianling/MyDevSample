package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/9/26 10:25
 * 类描述：
 * update 2017/9/26 对原有的RecycleView的BaseAdapter基类进行进一步抽象
 * 将BaseAdapter的基本功能抽象到该类，并将数据加载和上下拉刷新功能拆开到其子类{@link BaseRecycleViewPTRAdapter}中
 *
 * @see AbsBaseAdapter 抽象思路同该类
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbsBaseRecycleViewAdapter<DataType, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private final static long INTERVAL = 300;
    private final Activity mActivity;
    private final LayoutInflater inflater;
    protected List<DataType> mData = new ArrayList<>();
    private long lastClickTime;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    public AbsBaseRecycleViewAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
    }

    public AbsBaseRecycleViewAdapter(List<DataType> mData, Activity mActivity) {
        this(mActivity);
        this.mData = dataAssignment(mData);
    }

    public final Activity getActivity() {
        return mActivity;
    }

    protected
    @Nullable
    List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    protected DataType dataAssignment(@Nullable DataType dataType) {
        return dataType;
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateHolder(inflater, parent, viewType);
    }

    protected abstract VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    public final List<DataType> getData() {
        if (mData == null) {
            return null;
        } else {
            return new ArrayList<>(mData);
        }
    }

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

    protected int positionAssignment(int position) {
        return position;
    }

    /**
     * 当单项点击发生时可通过重写该方法对单项进行改动
     * <p>
     * 注意，在重写该方法后，若没有调用{@link #setOnItemClickListener(OnItemClickListener)}，则这个方法中的逻辑不会生效
     */
    @CallSuper
    protected void onItemLongClickEvent(VH holder) {
        onItemLongClickListener.onItemLongClick(positionAssignment(holder.getAdapterPosition()));
    }

    /**
     * 当单项长按发生时可通过重写该方法对单项进行改动
     * <p>
     * 注意，在重写该方法后，若没有调用{@link #setOnItemLongClickListener(OnItemLongClickListener)}，则这个方法中的逻辑不会生效
     */
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

    /**
     * 设置单项点击监听器
     */
    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置单项长按监听器
     */
    public final void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public DataType getItem(int position) {
        return position > mData.size() - 1 ? null : mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public final void addData(DataType dataType) {
        DataType data = dataAssignment(dataType);
        mData.add(data);
        notifyItemInserted(getItemCount() - 1);
    }

    public final void removeData(int position) {
        mData.remove(positionAssignment(position));
        notifyItemRemoved(positionAssignment(position));
    }

    public final void resetData(List<DataType> dataTypes) {
        mData = dataAssignment(dataTypes);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
