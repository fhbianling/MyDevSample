package com.bian.mydevsample.ui.indicatorbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    private List<DrawMsg> drawMsgList;
    private List<DrawMsg> intersectMsg;//和中心游标相交的数据
    private Paint mPaint;
    private float unselectedTextSize;
    private float selectedTextSize;
    private @ColorInt int unselectedTextColor;
    private @ColorInt int selectedTextColor;
    private int itemWidth;
    private int viewHeight;
    private int indicatorPageCount;//必须为奇数
    private int offSet;
    private float lastX;
    private float xCursor;
    private RectF centerRectF;
    private Path centerCursorPath = new Path();
    private float halfHeight;
    private int direction;//滚动方向，在action_up时，为該值赋值，为0表示idel,没有滚动，小于0表示向左，大于0表示向右

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
        if (centerRectF == null) {
            centerRectF = new RectF();
        }
        mPaint.setXfermode(null);
        intersectMsg.clear();
        int drawLeftX = 0;
        canvas.save();
        canvas.translate(-xCursor, 0);
        centerRectF.left = xCursor + itemWidth * offSet;
        centerRectF.right = centerRectF.left + itemWidth;
        centerRectF.top = 0;
        centerRectF.bottom = viewHeight;
        for (int i = 0; i < drawMsgList.size(); i++) {
            DrawMsg drawMsg = drawMsgList.get(i);
            boolean intersect = drawData(drawMsg,
                                         canvas,
                                         drawLeftX,
                                         centerRectF);
            drawLeftX += itemWidth;
            if (intersect) {
                intersectMsg.add(drawMsg);
            }
        }

        drawCursor(canvas, intersectMsg);
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

    public void setData(List<String> data) {
        if (drawMsgList == null) {
            drawMsgList = new ArrayList<>();
        } else {
            drawMsgList.clear();
        }
        ArrayList<String> strings = null;
        if (data != null) {
            strings = new ArrayList<>(data);
            offSet = indicatorPageCount / 2;
            for (int i = 0; i < offSet; i++) {
                strings.add(0, "");
            }
            for (int i = 0; i < offSet; i++) {
                strings.add("");
            }
            L.d(strings);
        }
        if (strings != null) {
            for (String string : strings) {
                drawMsgList.add(new DrawMsg(string));
            }
        }

        invalidate();
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                return true;
            case MotionEvent.ACTION_UP:
                xCursor = xCursor - (x - lastX);
                direction = (int) (x - lastX);
                autoScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                xCursor = xCursor - (x - lastX);
                xCursor = Math.max(xCursor, 0);
                xCursor = Math.min(xCursor,
                                   drawMsgList != null ? itemWidth * (drawMsgList.size() - indicatorPageCount) : 0);
                lastX = x;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void autoScroll() {
        float scrollTarget = 0;//0表示不需要自动滚动，小于0向左自动滚动，大于0向右自动滚动
        if (direction == 0) {
            float offSet = xCursor % itemWidth;
            scrollTarget = offSet - itemWidth / 2f;
        } else {
            scrollTarget = direction;
        }

        if (scrollTarget == 0) return;

        float targetXCursor;
        if (scrollTarget > 0) {
            targetXCursor = (float) (Math.ceil(xCursor / itemWidth) * itemWidth);
        } else {
            targetXCursor = (float) (Math.floor(xCursor / itemWidth) * itemWidth);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(xCursor, targetXCursor);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                xCursor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
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
        mPaint.setColor(selectedTextColor);
        mPaint.setTextSize(selectedTextSize);
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

    private boolean drawData(DrawMsg drawMsg, Canvas canvas, int leftX, RectF centerRectF) {
        mPaint.setTextSize(unselectedTextSize);
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
        unselectedTextSize = 32;
        selectedTextSize = 48;
        selectedTextColor = Color.WHITE;
        unselectedTextColor = Color.parseColor("#4994a8");
        indicatorPageCount = 5;
        intersectMsg = new ArrayList<>();
        setBackgroundColor(Color.parseColor("#c2f0f0"));
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
}
