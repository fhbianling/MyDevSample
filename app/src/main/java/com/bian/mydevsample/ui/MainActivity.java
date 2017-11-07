package com.bian.mydevsample.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bian.base.baseclass.AbsBaseActivity;
import com.bian.mydevsample.R;
import com.bian.mydevsample.ui.fragment.adaptertest.Fragment1;
import com.bian.mydevsample.ui.fragment.animtest1.Fragment4;
import com.bian.mydevsample.ui.fragment.button.Fragment6;
import com.bian.mydevsample.ui.fragment.floatinganim.Fragment5;
import com.bian.mydevsample.ui.fragment.pageanim1.Fragment2;
import com.bian.mydevsample.ui.fragment.pageanim2.Fragment3;

public class MainActivity extends AbsBaseActivity implements TabLayout.OnTabSelectedListener {
    private Fragment[] fragments = new Fragment[6];

    @Override
    protected void beforeOnCreate() {
        super.beforeOnCreate();
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if ("TextView".equals(name)) {
                    TextView textView = new TextView(context, attrs);
                    textView.setTypeface(Typeface.create("", Typeface.ITALIC));
                    return textView;
                }
                return null;
            }

            @Override
            public View onCreateView(View parent, String name, Context context,
                                     AttributeSet attrs) {
                if ("TextView".equals(name)) {
                    TextView textView = new TextView(context, attrs);
                    textView.setTypeface(Typeface.create("", Typeface.ITALIC));
                    return textView;
                }
                return null;
            }
        });
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        fragments[0] = new Fragment1();
        fragments[1] = new Fragment2();
        fragments[2] = new Fragment3();
        fragments[3] = new Fragment4();
        fragments[4] = new Fragment5();
        fragments[5] = new Fragment6();
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
