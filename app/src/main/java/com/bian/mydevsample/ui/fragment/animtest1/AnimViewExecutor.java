package com.bian.mydevsample.ui.fragment.animtest1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * author 边凌
 * date 2017/10/12 20:14
 * 类描述：
 */

public class AnimViewExecutor {
    static final int DEGREE_PER_ROUND = 360;
    //转几圈
    private float roundsCounts = 1;
    //单个动画持续时间
    private long duration = 500;

    private AnimView[] toAnimtedView;
    private int scaleWidth, scaleHeight;
    private int executeIndex;

    public AnimViewExecutor(View[] toAnimtedView) {
        this.toAnimtedView = new AnimView[toAnimtedView.length];
        for (int i = 0; i < toAnimtedView.length; i++) {
            this.toAnimtedView[i] = new AnimView(toAnimtedView[i]);
        }
    }

    public void execute() {
        executeIndex = 0;
        toAnimtedView[executeIndex].moveToDst(moveToListener);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                resume();
            }
            return false;
        }
    });

    public void setDstScale(int scaleWidth, int scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
    }

    private Animator.AnimatorListener moveToListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (executeIndex == toAnimtedView.length) return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public void setRoundsCounts(float roundsCounts) {
        this.roundsCounts = roundsCounts;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private void resume() {
        toAnimtedView[executeIndex].moveToOrigin();
        executeIndex++;
        if (executeIndex < toAnimtedView.length) {
            toAnimtedView[executeIndex].moveToDst(moveToListener);
        }
    }

    /**
     * author 边凌
     * date 2017/10/12 20:08
     * 类描述：
     */

    private class AnimView {
        private View view;
        private PointF dstPoint;
        private PointF originPoint;
        private float k, b;
        private int parentWidth;
        private int parentHeight;
        private int dstScale = 1;
        private int indexOfChild;
        private ViewGroup parent;

        AnimView(final View view) {
            this.view = view;
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                private boolean first = true;

                @Override
                public void onGlobalLayout() {
                    if (first) {
                        assignmentOriginPoint();
                        assignmentDstPoint();
                        assignmentKAndB();
                        assignmentScale();
                        first = false;
                    }
                }
            });
        }

        private void assignmentScale() {
            if (scaleWidth == 0 || scaleHeight == 0) return;

            if (view.getWidth() > view.getHeight()) {
                dstScale = scaleWidth / view.getWidth();
            } else {
                dstScale = scaleHeight / view.getHeight();
            }
        }

        private void assignmentKAndB() {
            k = (dstPoint.y - originPoint.y) / (dstPoint.x - originPoint.x);
            b = dstPoint.y - k * dstPoint.x;
        }

        private void assignmentDstPoint() {
            if (parentWidth == 0) {
                parent = getParent();
                parentWidth = parent.getWidth();
                parentHeight = parent.getHeight();
            }
            dstPoint = new PointF();
            dstPoint.x = parentWidth / 2 - view.getWidth() / 2;
            dstPoint.y = parentHeight / 2 - view.getHeight() / 2;
        }

        private void assignmentOriginPoint() {
            originPoint = new PointF();
            originPoint.x = view.getLeft();
            originPoint.y = view.getTop();
        }

        void moveToDst(Animator.AnimatorListener listener) {
            if (indexOfChild == 0) {
                indexOfChild = parent.indexOfChild(view);
            }
            parent.removeView(view);
            parent.addView(view);

            assignmentDstPoint();
            assignmentScale();
            ValueAnimator valueAnimator = ObjectAnimator.ofFloat(originPoint.x, dstPoint.x);
            valueAnimator.setDuration(duration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float x = (float) valueAnimator.getAnimatedValue();
                    float y = k * x + b;
                    float r = getRotationOfView(x);
                    float scale = getScale(x);
                    view.setX(x);
                    view.setY(y);
                    view.setRotation(r);
                    Log.d("moveTo", this.toString() + "Scale:" + scale);
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            });
            valueAnimator.addListener(listener);
            valueAnimator.start();
        }

        private ViewGroup getParent() {
            return (ViewGroup) view.getParent();
        }

        /**
         * 获取旋转的角度
         * 公式来源：
         * 360=Kr*Tmax
         * R=Kr*T;
         * Xmax-X0=K1*Tmax;
         * X=X0+K1*T;
         * 联立求解
         *
         * @param x view当前的x
         */
        private float getRotationOfView(float x) {
            return DEGREE_PER_ROUND * roundsCounts * getFactor(x);
        }

        /**
         * 计算原理同{@link #getRotationOfView(float)}
         */
        private float getScale(float x) {
            if (dstScale == 1) return dstScale;

            return (dstScale - 1) * getFactor(x) + 1;
        }

        private float getFactor(float x) {
            return (x - originPoint.x) / (dstPoint.x -
                    originPoint
                            .x);
        }

        void moveToOrigin() {
            parent.removeView(view);
            parent.addView(view, indexOfChild);
            assignmentDstPoint();
            ValueAnimator valueAnimator = ObjectAnimator.ofFloat(dstPoint.x, originPoint.x);
            valueAnimator.setDuration(duration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    float x = (float) valueAnimator.getAnimatedValue();
                    float y = k * x + b;
                    float r = getRotationOfView(x);
                    float scale = getScale(x);
                    Log.d("moveBack", this.toString() + "Scale:" + scale);

                    view.setX(x);
                    view.setY(y);
                    view.setRotation(r);
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }
            });
            valueAnimator.start();
        }
    }
}