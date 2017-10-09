package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.bian.base.baseclass.AbsBaseActivity;
import com.bian.mydevsample.R;

public class MainActivity extends AbsBaseActivity implements TabLayout.OnTabSelectedListener {
    private Fragment[] fragments = new Fragment[2];

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        fragments[0] = new Fragment1();
        fragments[1] = new Fragment2();
        showFragment(fragments[0], R.id.fragmentContainer);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        showFragment(fragments[position], R.id.fragmentContainer);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
