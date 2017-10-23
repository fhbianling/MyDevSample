package com.bian.mydevsample.ui.daggerstudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/10/23 16:50
 * 类描述：
 */

public class Dagger2StudyActivity extends BaseActivity{
    public static void start(Context context) {
        Intent starter = new Intent(context, Dagger2StudyActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected int bindLayoutId() {
        return R.layout.activity_dagger2study;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }
}
