package com.bian.mydevsample.ui.pageanim3qidian;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/12/4 10:58
 * 类描述：
 */

public class QiDianViewPagerAnimActivity extends BaseActivity {
    @Override
    protected int bindLayoutId() {
        return R.layout.activity_qidian;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                // TODO: 2017/12/4  
                return super.instantiateItem(container, position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position,
                                    @NonNull Object object) {
                // TODO: 2017/12/4  
                super.destroyItem(container, position, object);
            }
        });
    }
}
