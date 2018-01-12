package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
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
 * 将BaseAdapter的基本功能抽象到该类，并将数据加载和上下拉刷新功能拆开到其子类{@link BaseRVPtrAdapter}中
 *
 * @see AbsAdapter 抽象思路同该类
 */

public abstract class AbsRVAdapter<DataType, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private final static long INTERVAL = 300;
    private final Context CONTEXT;
    private final LayoutInflater INFLATER;
    private List<DataType> mData = new ArrayList<>();
    private long lastClickTime;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    public AbsRVAdapter(Context context) {
        this.CONTEXT = context;
        INFLATER = LayoutInflater.from(context);
    }

    public AbsRVAdapter(List<DataType> data, Activity context) {
        this(context);
        this.mData = dataAssignment(data);
    }

    public final Context getContext() {
        return CONTEXT;
    }

    public final LayoutInflater getINFLATER() {
        return INFLATER;
    }

    protected
    @Nullable
    List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    protected @Nullable
    DataType dataAssignment(@Nullable DataType dataType) {
        return dataType;
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH vh = onCreateHolder(INFLATER, parent, viewType);
        bindItemClickListener(vh);
        return vh;
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
        int itemViewType = getItemViewType(position);
        boolean isLast = position == getItemCount() - 1;
        DataType item = getItem(positionAssignment(position));
        if (item != null) {
            bindView(position, itemViewType, holder, item, isLast);
        }
    }

    protected int positionAssignment(int position) {
        return position;
    }

    protected abstract void bindView(int position, int viewType, @NonNull VH holder,
                                     @NonNull DataType item, boolean isLast);

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

    public
    @Nullable
    DataType getItem(int position) {
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

    public final void addData(List<DataType> data) {
        List<DataType> dataTypes = dataAssignment(data);
        if (dataTypes != null) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public final void removeData(int position) {
        mData.remove(positionAssignment(position));
        notifyItemRemoved(positionAssignment(position));
    }

    public final void resetData(List<DataType> dataTypes) {
        mData = dataAssignment(dataTypes);
        notifyDataSetChanged();
    }

    private boolean isFastClick() {
        boolean isFastClick = System.currentTimeMillis() - lastClickTime < INTERVAL;
        lastClickTime = System.currentTimeMillis();
        return isFastClick;
    }

    private void bindItemClickListener(final VH vh) {
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastClick()) return;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(positionAssignment(vh.getAdapterPosition()));
                }
            }
        });
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(positionAssignment(vh.getAdapterPosition()));
                }
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
