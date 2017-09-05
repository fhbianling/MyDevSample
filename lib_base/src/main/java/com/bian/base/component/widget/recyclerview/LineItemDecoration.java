package com.bian.base.component.widget.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bian.base.util.utilbase.UnitUtil;


/**
 * author 边凌
 * date 2017/6/19 16:10
 * 类描述：
 */

public class LineItemDecoration extends RecyclerView.ItemDecoration {
    private final static int DEFAULT_COLOR = Color.parseColor("#cccccc");
    private Paint dividerPaint = new Paint();
    private float dividerHeight;
    private Context context;
    private int leftMargin;
    private int rightMargin;

    public LineItemDecoration(Context context) {
        dividerHeight = UnitUtil.dp2px(context, 1);
        this.context = context;
        dividerPaint.setColor(DEFAULT_COLOR);
    }

    public void setDividerHeight(int dpValue) {
        dividerHeight = UnitUtil.dp2px(context, dpValue);
    }

    public void setDividerColor(@ColorInt int color) {
        dividerPaint.setColor(color);
    }

    public void setMargin(int leftDp, int rightDp) {
        this.leftMargin = UnitUtil.dp2px(context, leftDp);
        this.rightMargin = UnitUtil.dp2px(context, rightDp);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        left = left + this.leftMargin;
        if (right - this.rightMargin > 0) {
            right = right - this.rightMargin;
        }

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
//            L.d("left:"+left+",right:"+right+",top:"+top+",bottom:"+bottom);
            c.drawLine(left,top,right,bottom,dividerPaint);
        }
    }

}
