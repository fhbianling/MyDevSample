package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/10/9 15:46
 * 类描述：
 */

public class Fragment2 extends AbsBaseFragment {
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    protected void initView(View rootView) {

    }
}
