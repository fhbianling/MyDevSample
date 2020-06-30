package com.bian.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/9/26 9:52
 * 类描述：
 * update 2017/9/26 对原有的AbsListView的BaseAdapter基类进行进一步抽象
 * 将BaseAdapter的基本功能抽象到该类，并将数据加载和上下拉刷新功能拆开到其子类{@link BasePtrAdapter}中
 * <p>
 * 换句话说现在在不使用数据加载和上下拉刷新功能时,可以直接继承该类。
 */

public abstract class AbsAdapter<DataType, VH extends AbsAdapter.ViewHolder>
        extends BaseAdapter implements IAdapter<DataType> {
    private final LayoutInflater INFLATER;
    private final Context CONTEXT;
    private List<DataType> mOriginData;
    private List<DataType> mPretreatmentData;

    public AbsAdapter(Context context) {
        this.CONTEXT = context;
        INFLATER = LayoutInflater.from(context);
    }

    public AbsAdapter(List<DataType> data, Activity CONTEXT) {
        this(CONTEXT);
        resetData(data);
    }

    /**
     * 子类实现
     * 获得Holder
     */
    protected abstract @NonNull VH onCreateHolder(LayoutInflater inflater, ViewGroup parent,
                                                  int viewType);

    /**
     * 子类实现
     * 绑定数据到Holder
     */
    protected abstract void bindView(int position, int viewType, @NonNull VH holder,
                                     @NonNull DataType dataType, boolean isLast);

    /**
     * @see IAdapter#dataAssignment(List)
     */
    @Override
    public @Nullable List<DataType> dataAssignment(@Nullable List<DataType> data) {
        return data;
    }

    /**
     * @see IAdapter#resetData(List)
     */
    @Override
    public final void resetData(List<DataType> data) {
        ensureOriginDataNonNull();
        mOriginData = data;
        handleOriginDataUpdate();
    }

    /**
     * @see IAdapter#addData(List)
     */
    @Override
    public final void addData(List<DataType> data) {
        ensureOriginDataNonNull();
        if (data != null) {
            mOriginData.addAll(data);
        }
        handleOriginDataUpdate();
    }

    /**
     * @see IAdapter#addData(Object)
     */
    @Override
    public final void addData(DataType data) {
        ensureOriginDataNonNull();
        if (data != null) {
            mOriginData.add(data);
        }
        handleOriginDataUpdate();
    }

    /**
     * @see IAdapter#removeData(Object)
     */
    @Override
    public final void removeData(DataType data) {
        ensureOriginDataNonNull();
        mOriginData.remove(data);
        handleOriginDataUpdate();
    }

    /**
     * @see IAdapter#removeData(int)
     */
    @Override
    public final void removeData(int position) {
        ensureOriginDataNonNull();
        mOriginData.remove(position);
        handleOriginDataUpdate();
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

    @Override
    public final LayoutInflater getInflater() {
        return INFLATER;
    }

    @Override
    public final Context getContext() {
        return CONTEXT;
    }

    @Override
    public final int getCount() {
        return mPretreatmentData != null ? mPretreatmentData.size() : 0;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final DataType getItem(int i) {
        return indexValid(i, mPretreatmentData) ? mPretreatmentData.get(i) : null;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        int viewType = getItemViewType(i);
        VH holder;
        if (view == null) {
            holder = onCreateHolder(INFLATER, viewGroup, viewType);
            view = holder.getItemView();
            view.setTag(holder);
        } else {
            /*注意不能对getView获得的根视图设置tag*/
            //noinspection unchecked
            holder = (VH) view.getTag();
        }
        DataType item = getItem(i);
        if (item != null) {
            bindView(i, viewType, holder, item, i == getCount() - 1);
        }
        return view;
    }

    private void handleOriginDataUpdate() {
        if (mPretreatmentData != null) {
            mPretreatmentData.clear();
        }
        List<DataType> pretreatment = dataAssignment(mOriginData);
        if (pretreatment != null) {
            mPretreatmentData = new ArrayList<>(pretreatment);
        }
        notifyDataSetChanged();
    }

    private void ensureOriginDataNonNull() {
        if (mOriginData == null) {
            mOriginData = new ArrayList<>();
        }
    }

    private static boolean indexValid(int index, List<?> list) {
        return list != null && index >= 0 && index < list.size();
    }

    public static class ViewHolder {
        @SuppressWarnings("WeakerAccess")
        protected View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        @SuppressWarnings("WeakerAccess")
        public final View getItemView() {
            return itemView;
        }
    }
}
