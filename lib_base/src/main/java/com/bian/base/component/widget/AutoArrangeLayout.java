package com.bian.base.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import com.bian.base.R;

/**
 * author 边凌
 * date 2017/5/8 11:41
 * desc ${自动排列布局}
 */

public class AutoArrangeLayout extends ViewGroup {
    private int horizontalSpacing;
    private int verticalSpacing;
    private HashMap<View, Point> map = new HashMap<>();

    public AutoArrangeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoArrangeLayout);
        horizontalSpacing = ta.getDimensionPixelSize(R.styleable.AutoArrangeLayout_horizontalSpacing, 20);
        verticalSpacing = ta.getDimensionPixelSize(R.styleable.AutoArrangeLayout_verticalSpacing, 20);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            Point point = map.get(childAt);
            if (point != null) {
                childAt.layout(
                        point.x,
                        point.y,
                        point.x + childAt.getMeasuredWidth(),
                        point.y + childAt.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int length = getPaddingLeft();
        int rowHeight = 0;
        int lastLineHeight = getPaddingTop() + verticalSpacing;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);

            Point point = map.get(childAt);
            if (point == null) {
                point = new Point();
            }

            if (shouldNewLine(length, childAt)) {
                length = getPaddingLeft();
                lastLineHeight += rowHeight + verticalSpacing;
                point.x = length;
                point.y = lastLineHeight;
                map.put(childAt, point);
                length += childAt.getMeasuredWidth()+horizontalSpacing;
                rowHeight = childAt.getMeasuredHeight();
            } else {
                point.x = length;
                point.y = lastLineHeight;
                map.put(childAt, point);
                length += childAt.getMeasuredWidth() + horizontalSpacing;
                rowHeight = Math.max(childAt.getMeasuredHeight(), rowHeight);
            }
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int calcHeightMeasureSpec = heightMeasureSpec;

        if (heightMode != MeasureSpec.EXACTLY) {
            int calcHeight = lastLineHeight + rowHeight + verticalSpacing;
            calcHeightMeasureSpec = MeasureSpec.makeMeasureSpec(calcHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, calcHeightMeasureSpec);
    }

    private boolean shouldNewLine(int length, View childAt) {
        return childAt.getMeasuredWidth() + length + getPaddingLeft() + getPaddingRight() > getMeasuredWidth();
    }
}
