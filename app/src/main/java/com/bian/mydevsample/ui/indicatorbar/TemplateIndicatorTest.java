package com.bian.mydevsample.ui.indicatorbar;

import android.os.Bundle;
import android.view.View;

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
        TemplateIndicatorView indicatorView = findViewById(R.id.indicatorView);
        List<String> data = new ArrayList<>();
        data.add("封面");
        for (int i = 0; i < 28; i++) {
            data.add(String.valueOf(i + 1));
        }
        data.add("封底");
        indicatorView.setData(data);
    }

    @Override public void onClick(View v) {
        Object tag = v.getTag();
        L.d(tag);
    }
}
