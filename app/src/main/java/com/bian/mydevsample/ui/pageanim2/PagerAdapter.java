package com.bian.mydevsample.ui.pageanim2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/10/9 17:53
 * 类描述：
 */

class PagerAdapter extends android.support.v4.view.PagerAdapter {
    private LayoutInflater inflater;
    private int[] layoutRes = new int[]{R.layout.pager_1, R.layout.pager_2};
    private View[] views = new View[2];

    PagerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return views.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflateView(container, position);
        container.addView(view);
        return view;
    }

    private View inflateView(ViewGroup container, int position) {
        View inflate = inflater.inflate(layoutRes[position], container, false);
        views[position] = inflate;
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views[position]);
        views[position] = null;
    }
}
