package com.bian.mydevsample.ui.indicatorbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.bian.util.core.L;
import com.bian.util.core.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2018/6/29 10:29
 * 类描述：
 */
public class TemplateIndicatorView extends View {

    public static final int CURSOR_COLOR = Color.parseColor("#47d7c8");
    public static final int TRIANGLE_MARGIN_BOUNDARY = 15;//左右小三角和屏幕边界的距离
    public static final int TRIANGLE_WIDTH = 10;//左右小三角横向占用宽度
    public static final int TRIANGLE_HALF_HEIGHT = 10;//左右小三角高度的一半
    public static final int AUTO_SCROLL_DURATION = 300;//ms
    public static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#c2f0f0");
    public static final int DEFAULT_UNSELECTED_COLOR = Color.parseColor("#4994a8");
    public static final int DEFAULT_SELECT_COLOR = Color.WHITE;
    public static final int DEFAULT_INDICATOR_PAGE_COUNT = 5;
    public static final int DEFAULT_SELECTED_TEXT_SIZE = 48;
    public static final int DEFAULT_UNSELECTED_TEXT_SIZE = 32;
    private static final int COLOR_TRIANGLE = Color.parseColor("#44b1a0");
    private static int SCALED_TOUCH_SLOP;
    private List<DrawMsg> drawMsgList;
    private List<DrawMsg> intersectMsg;//和中心游标相交的数据
    private Paint mPaint;
    private @ColorInt int unselectedTextColor = DEFAULT_UNSELECTED_COLOR;
    private int itemWidth;
    private int viewHeight;
    private int indicatorPageCount = DEFAULT_INDICATOR_PAGE_COUNT;//必须为奇数
    private int halfViewIndicatorCount;//cursor左侧可见的drawMsg数量==cursor右侧可见的drawMsg数量
    private float lastX, downX;
    private float xCursor;//屏幕左侧可见元素相当于最初时View左侧的x坐标偏移量，这个值始终大于等于0，以View初始左侧为原点
    private RectF centerRectF;
    private Path centerCursorPath = new Path();
    private float halfHeight;
    private VelocityTracker mVelocityTracker;
    private Path leftTrianglePath;
    private Path rightTrianglePath;
    private OnSelectListener mOnSelectListener;
    private boolean enableScrollByOnScrollListener = true;

    public TemplateIndicatorView(Context context) {
        super(context);
        init();
    }

    public TemplateIndicatorView(Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override protected void onDraw(Canvas canvas) {
        if (drawMsgList == null) {
            return;
        }
        drawData(canvas);
        drawCursor(canvas, intersectMsg);
        drawDecor(canvas);
        canvas.restore();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = 0;
        int measuredHeight = 0;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                measuredWidth = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                measuredWidth = ScreenUtil.getScreenWidth(getContext());
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                measuredHeight = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                measuredHeight = (int) (measuredWidth / 5f);
                break;
        }
        viewHeight = measuredHeight;
        halfHeight = viewHeight / 2f;
        itemWidth = measuredWidth / indicatorPageCount;
        setMeasuredDimension(measuredWidth, measuredHeight);

    }

    @SuppressLint("ClickableViewAccessibility") @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                downX = x;
                return true;
            case MotionEvent.ACTION_UP:
                xCursor = xCursor - (x - lastX);
                autoScroll(Math.abs(downX - lastX) < SCALED_TOUCH_SLOP);
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
                xCursor = xCursor - (x - lastX);
                xCursor = Math.max(xCursor, 0);
                xCursor = Math.min(xCursor, getMaxXCursor());
                lastX = x;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    //定向处理使用PageSnapHelper的横向RecyclerView
    public void bindHorizontalPageSnapRecyclerView(final RecyclerView recyclerView,
                                                   final PagerSnapHelper snapHelper) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                View snapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (snapView != null) {
                    int position = recyclerView.getLayoutManager().getPosition(snapView);
                    if (enableScrollByOnScrollListener) {
                        xCursor = itemWidth * position;
                        invalidate();
                    }
                }
            }

            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!enableScrollByOnScrollListener) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        enableScrollByOnScrollListener = true;

                        View snapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                        if (snapView != null) {
                            int position = recyclerView.getLayoutManager().getPosition(snapView);
                            if (position != xCursor / itemWidth) {
                                xCursor = position * itemWidth;
                                invalidate();
                            }
                        }
                    }
                }

            }
        });
        setOnSelectListener(new OnSelectListener() {
            @Override public void onSelect(int position) {
                enableScrollByOnScrollListener = false;
                recyclerView.smoothScrollToPosition(position);
            }
        });
    }

    public void setItemCount(int count) {
        List<String> data = new ArrayList<>();
        data.add("封面");
        for (int i = 0; i < count - 2; i++) {
            data.add(String.valueOf(i + 1));
        }
        data.add("封底");
        L.d(data);
        if (drawMsgList == null) {
            drawMsgList = new ArrayList<>();
        } else {
            drawMsgList.clear();
        }
        halfViewIndicatorCount = indicatorPageCount / 2;
        for (int i = 0; i < halfViewIndicatorCount; i++) {
            data.add(0, "");
        }
        for (int i = 0; i < halfViewIndicatorCount; i++) {
            data.add("");
        }
        for (String string : data) {
            drawMsgList.add(new DrawMsg(string));
        }
        invalidate();
    }

    private int getMaxXCursor() {
        return drawMsgList != null ? (drawMsgList.size() - indicatorPageCount) * itemWidth : 0;
    }

    private void setOnSelectListener(OnSelectListener value) {
        this.mOnSelectListener = value;
    }

    private void drawData(Canvas canvas) {
        if (centerRectF == null) {
            centerRectF = new RectF();
        }
        mPaint.setXfermode(null);
        intersectMsg.clear();
        int drawLeftX = 0;
        canvas.save();
        canvas.translate(-xCursor, 0);
        centerRectF.left = xCursor + itemWidth * halfViewIndicatorCount;
        centerRectF.right = centerRectF.left + itemWidth;
        centerRectF.top = 0;
        centerRectF.bottom = viewHeight;
        for (int i = 0; i < drawMsgList.size(); i++) {
            DrawMsg drawMsg = drawMsgList.get(i);
            boolean intersect = drawSingleData(drawMsg,
                                               canvas,
                                               drawLeftX,
                                               centerRectF);
            drawLeftX += itemWidth;
            if (intersect) {
                intersectMsg.add(drawMsg);
            }
        }
    }

    private void drawDecor(Canvas canvas) {
        int leftItemIndex = (int) (xCursor / itemWidth);//左侧第一个可见元素的index
        //由于填充了数据，索引从0~halfViewIndicatorCount-1都是无意义的数据
        if (leftItemIndex > halfViewIndicatorCount) {
            //需要绘制左侧三角
            if (leftTrianglePath == null) {
                leftTrianglePath = new Path();
            } else {
                leftTrianglePath.reset();
            }

            float leftPointX = xCursor + TRIANGLE_MARGIN_BOUNDARY;
            leftTrianglePath.lineTo(leftPointX, halfHeight);
            leftTrianglePath.lineTo(leftPointX + TRIANGLE_WIDTH, halfHeight + TRIANGLE_HALF_HEIGHT);
            leftTrianglePath.lineTo(leftPointX + TRIANGLE_WIDTH, halfHeight - TRIANGLE_HALF_HEIGHT);
            leftTrianglePath.lineTo(leftPointX, halfHeight);
            leftTrianglePath.close();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(COLOR_TRIANGLE);
            canvas.drawPath(leftTrianglePath, mPaint);
        }
        if (leftItemIndex + indicatorPageCount < drawMsgList.size() - halfViewIndicatorCount) {
            //需要绘制右侧三角
            if (rightTrianglePath == null) {
                rightTrianglePath = new Path();
            } else {
                rightTrianglePath.reset();
            }

            float rightPointX = xCursor + itemWidth * indicatorPageCount - TRIANGLE_MARGIN_BOUNDARY;
            rightTrianglePath.lineTo(rightPointX, halfHeight);
            rightTrianglePath.lineTo(rightPointX - TRIANGLE_WIDTH,
                                     halfHeight + TRIANGLE_HALF_HEIGHT);
            rightTrianglePath.lineTo(rightPointX - TRIANGLE_WIDTH,
                                     halfHeight - TRIANGLE_HALF_HEIGHT);
            rightTrianglePath.lineTo(rightPointX, halfHeight);
            rightTrianglePath.close();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(COLOR_TRIANGLE);
            canvas.drawPath(rightTrianglePath, mPaint);
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void autoScroll(boolean isClick) {
        float targetXCursor = getTargetXCursor(isClick);
        if (targetXCursor == -1) return;
        if (xCursor == targetXCursor) return;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(xCursor, targetXCursor);
        valueAnimator.setDuration(AUTO_SCROLL_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                xCursor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOnSelectListener.onSelect((int) (xCursor / itemWidth));
            }
        });
        valueAnimator.start();
    }

    private float getTargetXCursor(boolean isClick) {
        float targetXCursor;
        if (!isClick) {
            //滚动
            float xVelocity = 0;
            if (mVelocityTracker != null) {
                mVelocityTracker.computeCurrentVelocity(1000);//1秒移动多少个像素
                xVelocity = mVelocityTracker.getXVelocity();
                L.d("xVelocity:" + xVelocity);
            }

            if (xVelocity == 0 && xCursor % itemWidth == 0) return -1;

            if (xCursor % itemWidth != 0 && xVelocity == 0) {
                xVelocity = itemWidth / 2f - xCursor % itemWidth;
            }

            if (xVelocity < 0) {
                L.d("direction < 0，向左自动滚动");
                targetXCursor = (float) (Math.ceil(xCursor / itemWidth) * itemWidth);
            } else {
                L.d("direction >= 0，向右自动滚动");
                targetXCursor = (float) (Math.floor(xCursor / itemWidth) * itemWidth);
            }
        } else {
            float xOffSet = (lastX / itemWidth - halfViewIndicatorCount) * itemWidth;
            targetXCursor = xOffSet + xCursor;
            targetXCursor = ((int) (targetXCursor / itemWidth)) * itemWidth;
        }

        targetXCursor = Math.max(targetXCursor, 0);
        targetXCursor = Math.min(targetXCursor, getMaxXCursor());

        return targetXCursor;
    }

    /**
     * 绘制游标
     */
    private void drawCursor(Canvas canvas, List<DrawMsg> intersectMsg) {
        calcCenterCursorPath();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(CURSOR_COLOR);
        canvas.drawPath(centerCursorPath, mPaint);
        int layerId = canvas.saveLayer(0,
                                       0,
                                       (drawMsgList.size() - 1) * itemWidth,
                                       viewHeight,
                                       null,
                                       Canvas.ALL_SAVE_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(CURSOR_COLOR);
        canvas.drawPath(centerCursorPath, mPaint);
        mPaint.setColor(DEFAULT_SELECT_COLOR);
        mPaint.setTextSize(DEFAULT_SELECTED_TEXT_SIZE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        for (DrawMsg drawMsg : intersectMsg) {
            canvas.drawText(drawMsg.msg,
                            drawMsg.textCenterX,
                            getBaseLine(),
                            mPaint);
        }
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    private void calcCenterCursorPath() {
        centerCursorPath.reset();
        centerCursorPath.lineTo(centerRectF.left, halfHeight);
        centerCursorPath.quadTo(centerRectF.left, 0, centerRectF.left + halfHeight, 0);
        centerCursorPath.lineTo(centerRectF.right - halfHeight, 0);
        centerCursorPath.quadTo(centerRectF.right, 0, centerRectF.right, halfHeight);
        centerCursorPath.quadTo(centerRectF.right,
                                viewHeight,
                                centerRectF.right - halfHeight,
                                viewHeight);
        centerCursorPath.lineTo(centerRectF.left + halfHeight, viewHeight);
        centerCursorPath.quadTo(centerRectF.left, viewHeight, centerRectF.left, halfHeight);
        centerCursorPath.close();
    }

    private boolean drawSingleData(DrawMsg drawMsg, Canvas canvas, int leftX, RectF centerRectF) {
        mPaint.setTextSize(DEFAULT_UNSELECTED_TEXT_SIZE);
        mPaint.setColor(unselectedTextColor);
        String text = drawMsg.msg;
        float measureWidth = mPaint.measureText(text);
        drawMsg.textStartY = getBaseLine();
        drawMsg.textCenterX = (leftX * 2 + itemWidth) / 2;
        canvas.drawText(text, (leftX * 2 + itemWidth) / 2, drawMsg.textStartY, mPaint);
        drawMsg.textWidth = measureWidth;
        //判断矩阵是否和中心矩阵相交
        drawMsg.intersect = drawMsg.getRectF().intersect(centerRectF);
        return drawMsg.intersect;
    }

    private int getBaseLine() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        return (int) ((viewHeight - textHeight) / 2f - fontMetrics.top);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        intersectMsg = new ArrayList<>();
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        if (SCALED_TOUCH_SLOP == 0) {
            SCALED_TOUCH_SLOP = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }
    }

    private class DrawMsg {
        private String msg;
        private RectF rectF;
        private boolean intersect = false;
        private float textCenterX;
        private float textStartY;
        private float textWidth;

        DrawMsg(String msg) {
            this.msg = msg;
        }

        private RectF getRectF() {
            if (rectF == null) {
                rectF = new RectF();
            }
            rectF.left = textCenterX;
            rectF.right = textCenterX + textWidth;
            rectF.top = textStartY;
            rectF.bottom = textStartY + 1;
            return rectF;
        }
    }

    public interface OnSelectListener {
        void onSelect(int position);
    }
}
