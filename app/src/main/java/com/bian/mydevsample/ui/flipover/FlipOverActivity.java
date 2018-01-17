package com.bian.mydevsample.ui.flipover;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2018/1/17 10:57
 * 类描述：翻页动画
 */

public class FlipOverActivity extends BaseActivity
        implements ViewTreeObserver.OnGlobalLayoutListener {
    private ImageView next;
    private FlipOverPageContainer container;

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_flip_over;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        next = findViewById(R.id.next);
        container = findViewById(R.id.container);
        container.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    @Override
    public void onGlobalLayout() {
        container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        Bitmap bitmap = convertViewToBitmap(next, next.getWidth(), next.getHeight());
        container.setNextBitmap(bitmap);
    }
}
