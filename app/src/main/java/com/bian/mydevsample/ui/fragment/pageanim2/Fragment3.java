package com.bian.mydevsample.ui.fragment.pageanim2;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.mydevsample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/10/10 14:51
 * 类描述：
 */

public class Fragment3 extends AbsBaseFragment implements ViewPager.OnPageChangeListener {
    private List<ViewMover> viewMovers = new ArrayList<>();

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }

    @Override
    protected void initView(View rootView) {
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        ViewGroup group = (ViewGroup) rootView;
        for (int i = 0; i < group.getChildCount(); i++) {
            View childAt = group.getChildAt(i);
            if (childAt instanceof TextView) {
                viewMovers.add(new ViewMover(childAt));
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (ViewMover viewMover : viewMovers) {
            viewMover.move(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }
}
