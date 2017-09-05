package com.bian.image.viewer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author 边凌
 * date 2017/8/3 17:00
 * 类描述： ViewPager+PhotoView在多点触控时可能引发越界异常从而崩溃，
 * 该自定义ViewPager用于修复这个问题
 * 由于这里采用依赖引入，不能修改PhotoView源码，这里只是简单的catch异常
 */

public class FixViewPager extends ViewPager {
    public FixViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ignored) {
        }
        return false;
    }
}
