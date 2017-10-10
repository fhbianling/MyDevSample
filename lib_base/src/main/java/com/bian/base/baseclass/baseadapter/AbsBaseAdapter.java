package com.bian.base.baseclass.baseadapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * 将BaseAdapter的基本功能抽象到该类，并将数据加载和上下拉刷新功能拆开到其子类{@link BasePTRAdapter}中
 * <p>
 * 换句话说现在在不使用数据加载和上下拉刷新功能时,可以直接继承该类。
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbsBaseAdapter<DataType, VH extends AbsBaseAdapter.ViewHolder>
        extends BaseAdapter {
    protected Activity mActivity;
    protected LayoutInflater inflater;
    private List<DataType> mData;
    private OnDataNotifyListener onDataNotifyListener;

    public AbsBaseAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
    }

    public AbsBaseAdapter(List<DataType> mData, Activity mActivity) {
        this(mActivity);
        this.mData = dataAssignment(mData);
    }

    public final Activity getActivity() {
        return mActivity;
    }

    /**
     * data改动，重写这个方法，可以对被赋值的data做出改动，可被重写。
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
     * 获得Holder
     */
    protected abstract
    @NonNull
    VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 展示数据
     */
    protected abstract void bindView(int position, int viewType, @NonNull VH holder,
                                     @NonNull DataType dataType, boolean isLast);

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
     * 设置data，注意，该方法仅仅会设置mData，不会做任何额外的操作，
     */
    protected final void setData(List<DataType> data) {
        mData = data;
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
        VH holder;
        if (view == null) {
            holder = onCreateHolder(inflater, viewGroup, viewType);
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

    public static class ViewHolder {
        private View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public final View getItemView() {
            return itemView;
        }
    }
}
