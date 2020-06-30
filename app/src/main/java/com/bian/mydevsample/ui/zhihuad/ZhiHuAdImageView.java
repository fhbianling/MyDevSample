package com.bian.mydevsample.ui.zhihuad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;

import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.bian.util.core.ScreenUtil;

/**
 * author 边凌
 * date 2017/12/5 15:53
 * 类描述：
 */

public class ZhiHuAdImageView extends AppCompatImageView
        implements ViewTreeObserver.OnScrollChangedListener {
    private Bitmap bitmap;
    private float k2;
    private int parentWidth = -1;
    private int parentHeight = -1;

    public ZhiHuAdImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getViewTreeObserver().addOnScrollChangedListener(this);
    }

    public void setSize(int pW, int pH) {
        this.parentWidth = pW;
        this.parentHeight = pH;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        int yOnScreen = (int) getY();
        float dy = k2 * yOnScreen;
        canvas.save();
        canvas.translate(0, -dy);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (parentWidth == -1 || parentWidth == 0) {
            parentWidth = ScreenUtil.getScreenWidth(getContext());
            parentHeight = ScreenUtil.getScreenHeight(getContext());
        }

        float newHeight = parentWidth * ((float) bm.getHeight() / bm.getWidth());
        Matrix matrix = new Matrix();
        float sX = (float) parentWidth / bm.getWidth();
        float sY = (float) newHeight / bm.getHeight();
        matrix.postScale(sX, sY);

        Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            bm.recycle();
        } catch (Exception e) {
            bitmap = bm;
        }

        k2 = (newHeight - getHeight()) / (parentHeight - getHeight());
        super.setImageBitmap(bitmap);
    }

    @Override
    public void onScrollChanged() {
        invalidate();
    }
}
