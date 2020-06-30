package com.bian.mydevsample.ui.pageanim2;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/12/4 11:13
 * 类描述：
 */

public class PagerAnimTest2 extends BaseActivity implements ViewPager.OnPageChangeListener {
    private List<ViewMover> viewMovers = new ArrayList<>();

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_pageranim2;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ViewPager viewPager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        ViewGroup group = findViewById(R.id.root);
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
