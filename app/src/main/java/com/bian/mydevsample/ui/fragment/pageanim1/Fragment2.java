package com.bian.mydevsample.ui.fragment.pageanim1;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.base.util.utilbase.ScreenUtil;
import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/10/9 15:46
 * 类描述：
 */

public class Fragment2 extends AbsBaseFragment implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private View target1;
    //当停止滑动时每个页面对应的目标终点坐标
    private Point[] pointsOfPage = new Point[3];
    private int state;
    private int[] indexCache = new int[2];

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    protected void initView(View rootView) {
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        target1 = rootView.findViewById(R.id.target1);
        final PagerAdapter pagerAdapter = new PagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);
        initPointsOfTarget1();
    }

    private void initPointsOfTarget1() {
        pointsOfPage[0] = new Point();
        pointsOfPage[0].set((int) target1.getX(), (int) target1.getY());
        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        int screenHeight = ScreenUtil.getScreenHeight(getContext());
        pointsOfPage[1] = new Point(screenWidth / 3, screenHeight / 4);
        pointsOfPage[2] = new Point(screenWidth * 3 / 5, screenHeight / 7);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int currentItem = viewPager.getCurrentItem();
        boolean isRight = position == currentItem;

        if (state == ViewPager.SCROLL_STATE_IDLE) return;

        Point endPoint = getPointOfEnd(currentItem,
                                       (state == ViewPager.SCROLL_STATE_DRAGGING) == isRight);
        //state==ViewPager.SCROLL_STATE_DRAGGING?isRight:!isRight
        //等价
        //state == ViewPager.SCROLL_STATE_DRAGGING) == isRight，简写，同或
        Point startPoint = getPointOfStart(currentItem);

        if (startPoint != null && endPoint != null && positionOffset != 0) {
            float offset = indexCache[1] < indexCache[0] ? (1 - positionOffset) : positionOffset;
            float x = startPoint.x + (endPoint.x - startPoint.x) * offset;
            float y = startPoint.y + (endPoint.y - startPoint.y) * offset;
            target1.setX(x);
            target1.setY(y);
        }
    }

    private Point getPointOfStart(int currentItem) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            indexCache[0] = currentItem;
        }
        return pointsOfPage[indexCache[0]];
    }

    private Point getPointOfEnd(int currentItem, boolean isRight) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            indexCache[1] = getEndIndex(currentItem, isRight);
        }
        return pointsOfPage[indexCache[1]];
    }

    private int getEndIndex(int currentItem, boolean isRight) {
        int endIndex = currentItem + (isRight ? 1 : -1);
        endIndex = Math.min(2, endIndex);
        endIndex = Math.max(0, endIndex);
        return endIndex;
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.state = state;
    }
}