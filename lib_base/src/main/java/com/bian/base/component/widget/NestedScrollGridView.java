package com.bian.base.component.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * author 边凌
 * date 2017/7/4 18:35
 * 类描述：
 */

public class NestedScrollGridView extends GridView{
    public NestedScrollGridView(Context context) {
        super(context);
    }

    public NestedScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NestedScrollGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
