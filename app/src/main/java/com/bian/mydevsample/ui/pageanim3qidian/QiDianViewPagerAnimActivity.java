package com.bian.mydevsample.ui.pageanim3qidian;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bian.util.core.L;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/12/4 10:58
 * 类描述：
 */

public class QiDianViewPagerAnimActivity extends BaseActivity
        implements ViewPager.OnPageChangeListener, View.OnTouchListener {
    public static final float SCALE_START = 1.0f;
    public static final float SCALE_END = 0.8f;
    private final static int[] COLOR = new int[]{Color.BLACK, Color.GREEN, Color.BLUE, Color.WHITE,
                                                 Color.MAGENTA};
    private boolean isDragging;
    private ViewPager viewPager;
    private float lastY, downY;
    private SparseArray<Float> yRecord = new SparseArray<>();
    private List<View> views = new ArrayList<>();

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_qidian;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(30);
        viewPager.setAdapter(new Adapter());
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnTouchListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                isDragging = true;
                shrinkView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                isDragging = false;
                resetState();
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                isDragging = false;
                break;
        }
    }

    private void resetState() {
        int index = viewPager.getCurrentItem();
        if (index - 1 >= 0) {
            enlargeView(index - 1);
        } else if (index + 1 < viewPager.getChildCount()) {
            enlargeView(index + 1);
        }
        enlargeView(index);
    }

    private void enlargeView(int index) {
        View childAt = views.get(index);
        if (childAt == null) {
            return;
        }
        if (childAt.getScaleX() == SCALE_START) return;
        childAt.setScaleX(SCALE_START);
        childAt.setScaleY(SCALE_START);
        Float yRecordOfChild = yRecord.get((int) childAt.getTag());
        if (childAt.getY() != yRecordOfChild) {
            childAt.setY(yRecordOfChild);
        }
    }

    private void shrinkView() {
        View childAt = getInFrontView();
        if (childAt == null) {
            return;
        }
        if (childAt.getScaleX() == SCALE_END) return;
        childAt.setScaleX(SCALE_END);
        childAt.setScaleY(SCALE_END);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                lastY = event.getY();
                View childAt = getInFrontView();
                if (childAt != null) {
                    Float y = yRecord.get((int) childAt.getTag());
                    if (y == null) {
                        yRecord.put((int) childAt.getTag(), childAt.getY());
                    }
                    if (isDragging) {
                        childAt.setTranslationY(lastY - downY);
                    }
                }
                break;
        }
        return false;
    }

    private View getInFrontView() {
        return views.get(viewPager.getCurrentItem());
    }

    private class Adapter extends PagerAdapter {

        @Override
        public int getCount() {
            return COLOR.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = null;
            if (position < views.size()) {
                view = views.get(position);
            }
            if (view != null) {
                return view;
            }
            TextView textView = new TextView(QiDianViewPagerAnimActivity.this);
            textView.setTextSize(32);
            textView.setText(String.format("ViewPager%d", position));
            textView.setTextColor(COLOR[position]);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.parseColor("#cccccc"));
            textView.setTag(position);
            views.add(textView);
            container.addView(textView);
            L.d("instantiateItem--" + textView.hashCode());
            return textView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {
            container.removeView((View) object);
            L.d("destroyItem--" + object.hashCode());
        }
    }
}
