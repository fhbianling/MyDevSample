package com.bian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.bian.base.R;


/**
 * author 边凌
 * date 2017/4/24 10:49
 * desc ${圆形的TextView}
 */

public class CircleTextView extends AppCompatTextView {

    private Paint fillPaint = new Paint();
    private
    @ColorInt
    int fillColor;
    private
    @ColorInt
    int borderColor = -1;
    private int borderWidthPx;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        fillColor = typedArray.getColor(R.styleable.CircleTextView_fill_color, Color.WHITE);
        borderColor = typedArray.getColor(R.styleable.CircleTextView_border_color, Color.WHITE);
        borderWidthPx = typedArray.getDimensionPixelSize(R.styleable.CircleTextView_border_width, -1);
        typedArray.recycle();
        setGravity(Gravity.CENTER);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(fillColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int max = Math.max(measuredWidth, measuredHeight);
        setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getWidth();
        int height = getWidth();
        int radius = Math.min(width - borderWidthPx * 2, height - borderWidthPx * 2) / 2;
        if (radius<0)return;
        int centerX = width / 2;
        int centerY = height / 2;

        if (borderWidthPx != -1 && borderColor != -1) {
            fillPaint.setColor(borderColor);
            canvas.drawCircle(centerX, centerY, radius + borderWidthPx, fillPaint);
            fillPaint.setColor(fillColor);
        }
        canvas.drawCircle(centerX, centerY, radius, fillPaint);
        super.onDraw(canvas);
    }

    public void setColor(@ColorInt int color) {
        this.fillColor = color;
        fillPaint.setColor(fillColor);
        invalidate();
    }

    public void setColorSrc(@ColorRes int colorSrc) {
        int color = ContextCompat.getColor(getContext(), colorSrc);
        setColor(color);
    }
}