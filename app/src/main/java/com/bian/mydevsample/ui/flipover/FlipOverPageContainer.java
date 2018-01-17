package com.bian.mydevsample.ui.flipover;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.bian.base.util.utilbase.L;


/**
 * author 边凌
 * date 2018/1/17 11:08
 * 类描述：
 */

public class FlipOverPageContainer extends FrameLayout {

    public static final int NONE = -1;
    //恢复动画进程最大值
    public static final float MAX_REVERT_RATE = 100f;
    //恢复动画的步进值，调整該值可以改变恢复动画速率
    public static final int REVERT_RATE_STEP = 10;
    private static int sTouchSlop;
    private PathComputer pathComputer;
    private PointF mMovePoint;
    private PointF mDownPoint;
    private Paint mPaint;
    private int revertProcessRate = NONE;
    private Bitmap bitmap;

    public FlipOverPageContainer(@NonNull Context context) {
        super(context);
        init();
    }

    public FlipOverPageContainer(@NonNull Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    private Path mRect;

    @Override
    public void draw(Canvas canvas) {
        if (mRect == null) {
            mRect = new Path();
            mRect.moveTo(0, 0);
            mRect.lineTo(0, getHeight());
            mRect.lineTo(getWidth(), getHeight());
            mRect.lineTo(getWidth(), 0);
            mRect.lineTo(0, 0);
            mRect.close();
        }
        super.draw(canvas);
        if (pathComputer != null) {
            Path backPath = pathComputer.getBackPath();
            mPaint.setColor(Color.RED);

            canvas.drawPath(backPath, mPaint);
            Path nextPagePath = pathComputer.getNextPagePath();
            canvas.clipPath(mRect);
            canvas.clipPath(nextPagePath, Region.Op.REPLACE);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, mPaint);
            }
        }
        if (revertProcessRate != NONE) {
            revert();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDownPoint == null) {
                    mDownPoint = new PointF();
                }
                mDownPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:

                boolean isClick = isClick(event);
                if (isClick) {
                    performClick();
                } else {
                    whenActionUp();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMovePoint == null) {
                    mMovePoint = new PointF();
                }
                mMovePoint.set(event.getX(), event.getY());
                computePath();
                invalidate();
                return true;
        }

        return true;
    }

    private void whenActionUp() {
        revert();
//        PathComputer.State state = pathInfo.getState();
//        switch (state) {
//            case NeedToGoLast:
//
//                break;
//            case NeedToGoNext:
//
//                break;
//            case NeedToRevert:
//
//                break;
//        }
    }

    public void setNextBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    private void revert() {
        if (revertProcessRate == NONE) {
            revertProcessRate = 0;
        } else {
            revertProcessRate += REVERT_RATE_STEP;
        }

        if (revertProcessRate <= MAX_REVERT_RATE) {
            pathComputer.computeRevert(revertProcessRate / MAX_REVERT_RATE);
            invalidate();
        } else {
            revertProcessRate = NONE;
        }
    }

    private boolean isClick(MotionEvent event) {
        double slop = Math.sqrt(Math.pow(event.getX() - mDownPoint.x,
                                         2) + Math.pow(event.getY() - mDownPoint.y,
                                                       2));
        L.d("slop:" + slop);
        if (sTouchSlop == 0) {
            sTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }
        return slop < sTouchSlop;
    }

    private void computePath() {
        if (pathComputer == null) {
            pathComputer = new PathComputer(getX() + getWidth(),
                                            getY() + getHeight());
        }
        pathComputer.computePathInfo(mDownPoint.x,
                                     mDownPoint.y,
                                     mMovePoint.x,
                                     mMovePoint.y);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
