package com.bian.mydevsample.ui.halfcolortext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * author 边凌
 * date 2018/1/5 16:12
 * 类描述：
 */

public class HalfColorTextView extends android.support.v7.widget.AppCompatTextView {

    private Shader shader;

    public HalfColorTextView(Context context) {
        super(context);
    }

    public HalfColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HalfColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shader == null) {
            shader = new LinearGradient(getWidth() / 2,
                                        0,
                                        getWidth() / 2,
                                        getHeight(),
                                        new int[]{Color.BLUE, Color.BLUE, Color.RED, Color.RED},
                                        new float[]{0f, 0.2f, 0.2f, 1f},
                                        Shader.TileMode.CLAMP);
        }
        getPaint().setShader(shader);
        super.onDraw(canvas);
        getPaint().setColor(Color.BLACK);
        float strokeWidth = getPaint().getStrokeWidth();
        getPaint().setStrokeWidth(5);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, getPaint());
        canvas.drawRect(getWidth() / 2 - 10, 0, getWidth() / 2 + 10, getHeight(), getPaint());
        getPaint().setStrokeWidth(strokeWidth);
    }
}
