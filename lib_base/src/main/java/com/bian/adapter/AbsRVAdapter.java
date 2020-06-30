package com.bian.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
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
@SuppressWarnings("WeakerAccess")
public abstract class AbsRVAdapter<DataType, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements IAdapter<DataType> {
    private final static long INTERVAL = 300;
    private final Context CONTEXT;
    private final LayoutInflater INFLATER;
    private List<DataType> mOriginData;
    private List<DataType> mPretreatmentData;
    private long lastClickTime;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemClickListener onItemClickListener;

    public AbsRVAdapter(Context context) {
        this.CONTEXT = context;
        INFLATER = LayoutInflater.from(context);
    }

    public AbsRVAdapter(List<DataType> data, Activity context) {
        this(context);
        resetData(data);
    }

    protected abstract VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract void bindView(int position, int viewType, @NonNull VH holder,
                                     @NonNull DataType item, boolean isLast);

    /**
     * @see IAdapter#dataAssignment(List)
     */
    @Override
    public @Nullable List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    @CallSuper
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        int itemViewType = getItemViewType(position);
        boolean isLast = position == getItemCount() - 1;
        DataType item = getItem(position);
        if (item != null) {
            bindView(position, itemViewType, holder, item, isLast);
        }
    }

    /**
     * @see IAdapter#getData()
     */
    @Override
    public final @Nullable List<DataType> getData() {
        return mOriginData;
    }

    /**
     * @see IAdapter#getPretreatmentData()
     */
    @Override
    public final @Nullable List<DataType> getPretreatmentData() {
        return mPretreatmentData;
    }

    /**
     * @see IAdapter#addData(Object)
     */
    @Override
    public final void addData(DataType data) {
        if (data != null) {
            ensureOriginDataNonNull();
            mOriginData.add(data);
            handleOriginDataUpdate(false);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    /**
     * @see IAdapter#addData(List)
     */
    @Override
    public final void addData(List<DataType> data) {
        if (data != null) {
            ensureOriginDataNonNull();
            mOriginData.addAll(data);
            handleOriginDataUpdate(true);
        }
    }

    /**
     * @see IAdapter#removeData(Object)
     */
    @Override
    public final void removeData(DataType data) {
        ensureOriginDataNonNull();
        mOriginData.remove(data);
        int originIndex = -1;
        if (mPretreatmentData != null) {
            originIndex = mPretreatmentData.indexOf(data);
        }
        handleOriginDataUpdate(false);
        if (originIndex == -1) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(originIndex);
        }
    }

    /**
     * @see IAdapter#removeData(int)
     */
    @Override
    public final void removeData(int position) {
        ensureOriginDataNonNull();
        if (mPretreatmentData != null && position < mPretreatmentData.size()) {
            DataType dataType = mPretreatmentData.get(position);
            removeData(dataType);
        }
    }

    /**
     * @see IAdapter#resetData(List)
     */
    @Override
    public final void resetData(List<DataType> dataTypes) {
        ensureOriginDataNonNull();
        mOriginData = dataTypes;
        handleOriginDataUpdate(true);
    }

    public @Nullable final DataType getItem(int position) {
        return indexValid(position, mPretreatmentData) ? mPretreatmentData.get(position) : null;
    }

    @Override
    public final int getItemCount() {
        return mPretreatmentData != null ? mPretreatmentData.size() : 0;
    }

    @Override
    public final Context getContext() {
        return CONTEXT;
    }

    @Override
    public final LayoutInflater getInflater() {
        return INFLATER;
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH vh = onCreateHolder(INFLATER, parent, viewType);
        bindItemClickListener(vh);
        return vh;
    }

    /**
     * 设置单项点击监听器
     */
    @SuppressWarnings("unused")
    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置单项长按监听器
     */
    @SuppressWarnings("unused")
    public final void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private void handleOriginDataUpdate(boolean notify) {
        if (mPretreatmentData != null) {
            mPretreatmentData.clear();
        }
        List<DataType> pretreatment = dataAssignment(mOriginData);
        if (pretreatment != null) {
            mPretreatmentData = new ArrayList<>(pretreatment);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    private void ensureOriginDataNonNull() {
        if (mOriginData == null) {
            mOriginData = new ArrayList<>();
        }
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
                    onItemClickListener.onItemClick(vh.getAdapterPosition());
                }
            }
        });
        vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(vh.getAdapterPosition());
                }
                return true;
            }
        });
    }

    private static boolean indexValid(int index, List<?> list) {
        return list != null && index >= 0 && index < list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
