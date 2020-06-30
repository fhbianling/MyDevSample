package com.bian.mydevsample.ui.adapterbasetest;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.adapter.AbsRVAdapter;
import com.bian.adapter.RvDefaultHolder;
import com.bian.mydevsample.R;
import com.bian.util.core.ToastUtil;

/**
 * author 边凌
 * date 2018/6/20 14:30
 * 类描述：
 */
public class TestAdapter extends AbsRVAdapter<Integer, RvDefaultHolder> {

    public TestAdapter(Context context) {
        super(context);
    }

    @NonNull @Override
    protected RvDefaultHolder onCreateHolder(LayoutInflater inflater, ViewGroup parent,
                                             int viewType) {
        return new RvDefaultHolder(inflater.inflate(R.layout.item_base_test, parent, false)) {
            @Override protected void init(View root) {
                root.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        ToastUtil.showToastShort("移除position为" + getAdapterPosition() + "的数:" +
                                                         getItem(getAdapterPosition()));
                        removeData(getAdapterPosition());
                    }
                });
            }
        };
    }

    @Override
    protected void bindView(int position, int viewType, @NonNull RvDefaultHolder holder,
                            @NonNull Integer integer, boolean isLast) {
        holder.setText(R.id.tv, String.valueOf(integer));
    }

}
