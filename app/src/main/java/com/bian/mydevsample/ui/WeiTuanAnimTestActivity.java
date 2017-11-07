package com.bian.mydevsample.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bian.base.util.utilbase.L;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/10/19 15:56
 * 类描述：
 */

public class WeiTuanAnimTestActivity extends BaseActivity implements Animator.AnimatorListener {
    private ImageView img1;
    private ImageView img2;
    private PointF dstPoint;
    private PointF originPoint;
    private float dstScaleX;
    private float dstScaleY;
    private int originWidth;
    private int originHeight;
    private float originScaleX;
    private float originScaleY;
    private AnimatorSet startAnim;

    public static void start(Context context) {
        Intent starter = new Intent(context, WeiTuanAnimTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_wetuananimtest;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
    }

    public void reset(View view) {
        img1.setX(originPoint.x);
        img1.setY(originPoint.y);
        img1.setRotation(0);
        img1.setScaleX(1);
        img1.setScaleY(1);
        logInfo("afterReset:");
    }

    public void function(View view) {
        originWidth = img1.getWidth();
        originHeight = img1.getHeight();
        originScaleX = img1.getScaleX();
        originScaleY = img1.getScaleY();

        img1.setX(img2.getX());
        img1.setY(img2.getY());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getLayoutParams());
        layoutParams.width = img2.getWidth();
        layoutParams.height = img2.getHeight();
        img1.setLayoutParams(layoutParams);
        img1.setScaleX(1);
        img1.setScaleY(1);
        logInfo("afterFunction:");
    }

    public void functionBeforeBack(View view) {
        img1.setX(dstPoint.x);
        img1.setY(dstPoint.y);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(img1.getLayoutParams());
        layoutParams.width = originWidth;
        layoutParams.height = originHeight;
        img1.setLayoutParams(layoutParams);
        img1.setScaleX(originScaleX);
        img1.setScaleY(originScaleY);
    }

    public void back(View view) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(img1,
                                                          "x",
                                                          dstPoint.x,
                                                          originPoint.x);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(img1,
                                                          "y",
                                                          dstPoint.y,
                                                          originPoint.y);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(img1,
                                                                 "rotation",
                                                                 720,
                                                                 0);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(img1,
                                                               "scaleX",
                                                               dstScaleX,
                                                               1);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(img1,
                                                               "scaleY",
                                                               dstScaleY,
                                                               1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(xAnimator,
                                 yAnimator,
                                 rotationAnimator,
                                 scaleXAnimator,
                                 scaleYAnimator);
        animatorSet.setDuration(200);
        animatorSet.addListener(this);
        animatorSet.start();
    }

    public void start(View view) {
        dstScaleX = ((float) img2.getWidth()) / img1.getWidth();
        dstScaleY = ((float) img2.getHeight()) / img1.getHeight();
        View v = (View) img1.getParent();

        dstPoint = new PointF((v.getWidth() - img1.getWidth()) / 2,
                              (v.getHeight() - img1.getHeight()) / 2);
        originPoint = new PointF(img1.getLeft(), img1.getTop());
        logInfo("origin:");
        L.d("img2-width:" + img2.getWidth() + ",height:" + img2.getHeight() + ",left:" + img2.getLeft() + ",top:" + img2
                .getTop());
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(img1,
                                                          "x",
                                                          originPoint.x,
                                                          dstPoint.x);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(img1,
                                                          "y",
                                                          originPoint.y,
                                                          dstPoint.y);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(img1,
                                                                 "rotation",
                                                                 0,
                                                                 720);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(img1,
                                                               "scaleX",
                                                               1,
                                                               dstScaleX);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(img1,
                                                               "scaleY",
                                                               1,
                                                               dstScaleY);
        startAnim = new AnimatorSet();
        startAnim.playTogether(xAnimator,
                               yAnimator,
                               rotationAnimator,
                               scaleXAnimator,
                               scaleYAnimator);
        startAnim.setDuration(200);
        startAnim.addListener(this);
        startAnim.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        String suffix = "afterAnim:";
        logInfo(suffix);
        if (animation == startAnim) {

        }
    }

    private void logInfo(String suffix) {
        L.d(suffix + "Width:" + img1.getWidth() + ",Height:" + img1.getHeight() + ",Left:" + img1
                .getLeft() + ",Top:" + img1.getTop());
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
