package com.bian.mydevsample.ui.fragment.button;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bian.base.baseclass.baseadapter.AbsBaseAdapter;
import com.bian.base.baseclass.baseadapter.ListDefaultHolder;
import com.bian.mydevsample.R;

import java.util.List;

/**
 * author 边凌
 * date 2017/10/13 15:20
 * 类描述：
 */

public class Adapter extends AbsBaseAdapter<ButtonDesc, ListDefaultHolder> {
    public Adapter(List<ButtonDesc> mData, Activity mActivity) {
        super(mData, mActivity);
    }

    @NonNull
    @Override
    protected ListDefaultHolder onCreateHolder(LayoutInflater inflater, ViewGroup parent,
                                               int viewType) {
        return new ListDefaultHolder(inflater.inflate(R.layout.item_button, parent, false));
    }

    @Override
    protected void bindView(int position, int viewType, @NonNull ListDefaultHolder holder,
                            @NonNull ButtonDesc buttonDesc, boolean isLast) {
        holder.setText(R.id.tv, buttonDesc.name);

    }
}
