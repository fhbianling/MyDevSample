package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

public class Fragment2 extends AbsBaseFragment implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    protected void initView(View rootView) {
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
