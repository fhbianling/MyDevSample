package com.bian.mydevsample.ui.qqclean;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/12/6 10:02
 * 类描述：
 */

public class QQCleanAnimView extends View {

    public static final int DEFAULT_SIDE_LENGTH = 200;
    private final static long PER_ROUND_TIME = 1300;
    private int strokeWidth;
    private int[] center = new int[2];
    private Paint paint = new Paint();
    private int circleColor = Color.parseColor("#CCCCCC");
    private int baseRadarColor = Color.parseColor("#159fff");
    private SweepGradient shader;
    private RectF arc = new RectF();
    private int rotateDegrees = 0;
    private int updateInterval = 20;
    private int degreeInterval;
    private boolean stop = true;

    public QQCleanAnimView(Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public QQCleanAnimView(Context context) {
        super(context);
        init(null);
    }

    public boolean isRunning() {
        return !stop;
    }

    private void init(AttributeSet attrs) {
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        degreeInterval = (int) (360 / (PER_ROUND_TIME / updateInterval));
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                                                                        R.styleable.QQCleanAnimView);
            strokeWidth = typedArray.getDimensionPixelOffset(R.styleable.QQCleanAnimView_strokeWidth,
                                                             0);
            typedArray.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rotateDegrees += degreeInterval;
        if (stop) {
            rotateDegrees = -150;
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.save();
        canvas.rotate(rotateDegrees, center[0], center[1]);
        canvas.drawArc(arc, 0, 360, false, paint);
        canvas.restore();

        if (!stop) {
            postInvalidateDelayed(updateInterval);
        }
    }

    public void start() {
        stop = false;
        invalidate();
    }

    public void stop() {
        stop = true;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                widthSize = DEFAULT_SIDE_LENGTH;
                break;
            case MeasureSpec.AT_MOST:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
                heightSize = DEFAULT_SIDE_LENGTH;
                break;
            case MeasureSpec.AT_MOST:
                break;
        }

        int sideLength = Math.min(widthSize, heightSize);

        valueAssignment(sideLength);

        setMeasuredDimension(sideLength, sideLength);
    }

    private void valueAssignment(int sideLength) {
        if (strokeWidth == 0) {
            strokeWidth = sideLength / 6;
        }
        center[0] = sideLength / 2;
        center[1] = sideLength / 2;
        arc.left = strokeWidth / 2;
        arc.right = sideLength - strokeWidth / 2;
        arc.top = strokeWidth / 2;
        arc.bottom = sideLength - strokeWidth / 2;

        if (shader == null) {
            shader = new SweepGradient(center[0],
                                       center[1],
                                       new int[]{circleColor, baseRadarColor, circleColor,
                                                 circleColor},
                                       new float[]{0f, 1 / 6f, 1 / 6f, 1f});
        }
        paint.setShader(shader);
    }
}
