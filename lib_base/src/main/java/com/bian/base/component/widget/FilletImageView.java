package com.bian.base.component.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bian.base.util.utilbase.UnitUtil;

/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 *
 * @author caizhiming
 */
public class FilletImageView extends AppCompatImageView {

    private Paint paint;
    private Rect rectSrc;
    private Rect rectDest;

    public FilletImageView(Context context) {
        this(context, null);
    }

    public FilletImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilletImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    /**
     * 绘制圆角矩形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = getRoundBitmap(bitmap, UnitUtil.dp2px(getContext(), 5));
            if (rectSrc == null) {
                rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
                rectDest = new Rect(0, 0, getWidth(), getHeight());
            } else {
                rectSrc.set(0, 0, b.getWidth(), b.getHeight());
                rectDest.set(0, 0, getWidth(), getHeight());
            }
            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取圆角矩形图片方法
     *
     * @param bitmap
     * @param roundPx,一般设置成14
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        if (bitmap == null) {
            throw new NullPointerException();
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }
}  