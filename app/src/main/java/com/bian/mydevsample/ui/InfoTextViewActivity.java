package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.view.View;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/11/27 15:01
 * 类描述：
 */

public class InfoTextViewActivity extends BaseActivity {
    @Override
    protected int bindLayoutId() {
        return R.layout.activity_info_text_view;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    public void show(View view) {
        InfoTextViewSampleActivity.start(this);
    }
}
