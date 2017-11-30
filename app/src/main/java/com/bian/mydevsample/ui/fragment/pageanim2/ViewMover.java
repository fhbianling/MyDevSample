package com.bian.mydevsample.ui.fragment.pageanim2;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bian.base.util.utilbase.ScreenUtil;


/**
 * author 边凌
 * date 2017/10/10 15:19
 * 类描述：
 */

class ViewMover {
    private static PointF sZoomCenter;
    private float k;
    private float b;
    private float minX;
    private View view;
    private PointF originPoint;

    ViewMover(View view) {
        this.view = view;
        view.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (k == 0) {
                        init(ViewMover.this.view);
                    }
                }
            });
    }

    private void init(View view) {
        if (sZoomCenter == null) {
            sZoomCenter = new PointF(ScreenUtil.getScreenWidth(view.getContext()),
                                     ScreenUtil.getScreenHeight(view.getContext()) / 2);
        }
        originPoint = new PointF(view.getX(), view.getY());
        k = (sZoomCenter.y - originPoint.y) / (sZoomCenter.x - originPoint.x);
        b = sZoomCenter.y - k * sZoomCenter.x;

        if (k > 0) {
            PointF minPoint = new PointF();
            minPoint.y = 0 - view.getHeight();
            minX = (minPoint.y - b) / k;
        } else {
            minX = 0 - view.getWidth();
        }
    }

    void move(double positionOffset) {
        if (positionOffset == 0) return;
        float x = (float) (positionOffset * (minX - originPoint.x) + originPoint.x);
        float y = x * k + b;
        view.setX(x);
        view.setY(y);
    }
}
