package com.bian.mydevsample.ui.indicatorbar;

import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bian.adapter.RvDefaultHolder;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;
import com.bian.util.core.L;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2018/6/29 11:18
 * 类描述：
 */
public class TemplateIndicatorTest extends BaseActivity implements View.OnClickListener {
    @Override protected int bindLayoutId() {
        return R.layout.activity_template_indicator_test;
    }

    @Override protected void initView(Bundle savedInstanceState) {
        final List<String> data = new ArrayList<>();
        data.add("封面");
        for (int i = 0; i < 28; i++) {
            data.add(String.valueOf(i + 1));
        }
        data.add("封底");
        RecyclerView mRv = findViewById(R.id.mRv);
        mRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textView = new TextView(TemplateIndicatorTest.this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                 ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(64);
                textView.setTextColor(Color.BLACK);
                return new RvDefaultHolder(textView);
            }

            @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                String s = data.get(position);
                TextView textView = (TextView) holder.itemView;
                textView.setText(s);
            }

            @Override public int getItemCount() {
                return data.size();
            }
        };
        mRv.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRv);
        TemplateIndicatorView indicatorView = findViewById(R.id.indicatorView);
        indicatorView.bindHorizontalPageSnapRecyclerView(mRv, snapHelper);
        indicatorView.setItemCount(adapter.getItemCount());
    }

    @Override public void onClick(View v) {
        Object tag = v.getTag();
        L.d(tag);
    }
}
