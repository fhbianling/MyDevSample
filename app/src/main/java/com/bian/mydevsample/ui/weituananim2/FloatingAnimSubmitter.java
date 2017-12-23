package com.bian.mydevsample.ui.weituananim2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author 边凌
 * date 2017/10/13 11:15
 * 类描述：用于执行滚动通知动画的工具类
 * <p>
 * 对外暴露的核心方法：
 *
 * @see #execute(View)
 * @see #cancel(View)
 * @see #setInitHeight(int)
 * @see #setPause()
 * @see #setResume()
 * @see #isPause()
 */

// fixme: 2017/10/13 当执行动画的队列中包含的view高度不等时，显示会出现重叠的现象
public class FloatingAnimSubmitter {
    /**
     * view在屏幕上的静止滞留时间
     */
    public static final long DELAY_DURATION = 3000;
    /**
     * 进入屏幕中心和从屏幕中心移除到屏幕外的动画持续时间
     */
    public static final int ANIM_DURATION = 300;
    /**
     * 同时显示的浮动条槽位数，将槽位数设为1可以禁用槽位效果
     */
    private static final int COUNT = 3;
    private Activity activity;
    private SlotExecutor[] slotExecutor = new SlotExecutor[COUNT];
    private int[] heightValue = new int[COUNT];
    private int initHeight;
    private boolean pause;

    public FloatingAnimSubmitter(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置所有View的初始高度，换句话说即最顶部的那条view的左上点的y坐标
     */
    public void setInitHeight(int initHeight) {
        this.initHeight = initHeight;
    }

    /**
     * 执行动画，这个类只关注view动画,view动画的点击事件应该在外面写
     *
     * @param view 要执行动画的view
     */
    public void execute(View view) {
        int viewAnimSlotIndex = calcSlotIndex();
        int heightValue = calcViewAnimHeight(viewAnimSlotIndex);

        if (indexValid(viewAnimSlotIndex)) {
            slotExecutor[viewAnimSlotIndex].execute(view, heightValue);
        }
    }

    /**
     * 取消动画，调用该方法时，会查找该类保存的view集合是否包含该view
     * 若包含且该view正在执行动画的过程中，则会对该view执行从其当前位置向左移到屏幕外的动画
     *
     * @param view 对应execute传入的view
     */
    public void cancel(View view) {
        for (SlotExecutor executor : slotExecutor) {
            ViewDescribe viewDescribe = executor.viewDescribe;
            if (viewDescribe == null || viewDescribe.view == null) {
                continue;
            }
            if (viewDescribe.view.equals(view)) {
                executor.cancel();
                break;
            }
        }
    }

    /**
     * 考虑到当view点击事件发生时可能会打开新界面，
     * 该方法暂停当前所有动画
     */
    public void setPause() {
        pause = true;
        for (SlotExecutor executor : slotExecutor) {
            if (executor != null) {
                executor.setPause();
            }
        }
    }

    /**
     * 考虑到当view点击事件发生时可能会打开新界面，
     * 该方法返回当前所有动画是否处于暂停状态
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * 考虑到当view点击事件发生时可能会打开新界面，
     * 该方法恢复到动画的正常流程中
     */
    public void setResume() {
        pause = false;
        for (SlotExecutor executor : slotExecutor) {
            if (executor != null) {
                executor.setResume();
            }
        }
    }

    /**
     * 验证槽位序号有效
     */
    private boolean indexValid(int viewAnimSlotIndex) {
        return viewAnimSlotIndex < slotExecutor.length;
    }

    /**
     * 计算view应该被插入的槽位的序号
     * <p>
     * 逻辑：内部的view集合的大小最小的槽位既是view应该被插入的槽位，以达到看起来弹幕在高度上平均分布的效果
     */
    private int calcSlotIndex() {
        int result = 0;
        for (int i = 0; i < slotExecutor.length; i++) {
            initSlotIfNecessary(i);
            if (slotExecutor[result].getViewCount() > slotExecutor[i]
                    .getViewCount()) {
                result = i;
            }
            heightValue[i] = slotExecutor[i].getHeightOfView();
        }
        return result;
    }

    /**
     * 根据槽位序号计算该view执行动画时的高度
     */
    private int calcViewAnimHeight(int viewAnimSlotIndex) {
        int heightValue = initHeight;
        for (int i = 0; i < viewAnimSlotIndex; i++) {
            heightValue += this.heightValue[i];
        }
        return heightValue;
    }

    private void initSlotIfNecessary(int i) {
        if (slotExecutor[i] == null) {
            slotExecutor[i] = new SlotExecutor(activity);
        }
    }

    /**
     * author 边凌
     * date 2017/10/13 10:17
     * 类描述：单个浮动条槽位,描述该槽位中的view集合的动画执行逻辑
     */

    private static class SlotExecutor implements Animator.AnimatorListener {
        private static int sStartX;
        private ViewGroup decorView;
        private Set<ViewDescribe> viewSet = new LinkedHashSet<>();
        //        private ValueAnimator valueAnimator;
        private ViewDescribe viewDescribe;
        private AnimatorSet animatorSet;
        private boolean executeDelayToGetViewWidth;
        private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (viewDescribe != null && viewDescribe.viewWidth == 0) {
                    viewDescribe.viewWidth = viewDescribe.view.getWidth();
                    viewDescribe.view.getViewTreeObserver().removeOnGlobalLayoutListener(
                            onGlobalLayoutListener);
                    realExecute();
                }
            }
        };
        private Animator.AnimatorListener cancelListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatorSet != null) {
                    animatorSet.removeListener(cancelListener);
                    animatorSet.addListener(SlotExecutor.this);
                }
                animationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        SlotExecutor(Activity activity) {
            decorView = (ViewGroup) activity.getWindow().getDecorView();
        }

        void execute(final View view, final int yOfView) {
            if (view == null) {
                throw new NullPointerException();
            }

            ViewDescribe viewDescribe = new ViewDescribe(view, yOfView);
            //加入到view集合，该集合包含的是正在播放以及即将被播放的view
            viewSet.add(viewDescribe);

            //非动画播放状态则开始播放，否则什么都不做，等待当前view的播放完成
            boolean animatorDontPlaying = animatorSet == null || (!animatorSet.isStarted() && !animatorSet
                    .isRunning());
            if (animatorDontPlaying && !executeDelayToGetViewWidth) {
                addToParentAndStartAnim(viewDescribe);
            }
        }

        int getHeightOfView() {
            if (viewSet.iterator().hasNext()) {
                View view = viewSet.iterator().next().view;
                if (view.getHeight() == 0) {
                    view.measure(0, 0);
                    return view.getMeasuredHeight();
                } else {
                    return view.getHeight();
                }
            }
            return 0;
        }

        int getViewCount() {
            return viewSet.size();
        }

        /**
         * 动画逻辑 先移动到中间，停留3秒，再移除边界
         */
        private void addToParentAndStartAnim(final ViewDescribe viewDescribe) {
            View view = viewDescribe.view;
            decorView.addView(view,
                              new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                         ViewGroup.LayoutParams.WRAP_CONTENT));
            this.viewDescribe = viewDescribe;
            this.viewDescribe.view.getViewTreeObserver()
                                  .addOnGlobalLayoutListener(onGlobalLayoutListener);
        }

        private void realExecute() {
            executeDelayToGetViewWidth = true;
            final View view = viewDescribe.view;
            int yOfView = viewDescribe.y;
            view.setY(yOfView);
            if (sStartX == 0) {
                sStartX = view.getContext().getResources().getDisplayMetrics().widthPixels;
            }
            int endXOfLeftScreen = -view.getWidth();
            int endXOfMiddleScreen = sStartX / 2 - viewDescribe.viewWidth / 2;
            view.setX(sStartX);
            ObjectAnimator objectAnimator0 = ObjectAnimator.ofFloat(view,
                                                                    "x",
                                                                    sStartX,
                                                                    endXOfMiddleScreen);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view,
                                                                    "x",
                                                                    endXOfMiddleScreen,
                                                                    endXOfLeftScreen);
            objectAnimator0.setDuration(ANIM_DURATION);
            objectAnimator2.setDuration(ANIM_DURATION);
//            if (animatorSet == null) {
                animatorSet = new AnimatorSet();
                animatorSet.addListener(this);
//            }
//            animatorSet.play(objectAnimator0);
            animatorSet.play(objectAnimator2).after(DELAY_DURATION).after(objectAnimator0);
            animatorSet.start();
        }

        private void cancel() {
            if (animatorSet == null) {
                return;
            }
            animatorSet.removeListener(this);
            animatorSet.cancel();
            if (viewDescribe == null) {
                return;
            }
            View view = viewDescribe.view;
            float x = view.getX();
            int endXOfLeftScreen = -viewDescribe.viewWidth;
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "x", x, endXOfLeftScreen);
            objectAnimator.setDuration(ANIM_DURATION);
            animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSet.addListener(cancelListener);
            animatorSet.start();
        }

        private void executeRemain() {
            Iterator<ViewDescribe> iterator = viewSet.iterator();
            if (iterator.hasNext()) {
                addToParentAndStartAnim(iterator.next());
            }
        }

        private void setPause() {
            if (animatorSet != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    animatorSet.pause();
                }
            }
        }

        private void setResume() {
            if (animatorSet != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    animatorSet.resume();
                }
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationEnd();
        }

        private void animationEnd() {
            executeDelayToGetViewWidth = false;
            if (viewDescribe != null) {
                decorView.removeView(viewDescribe.view);
                viewSet.remove(viewDescribe);
            }
            executeRemain();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    /**
     * 描述view和其执行动画时应该处于的y坐标
     */
    private static class ViewDescribe {
        private View view;
        private int y;
        private int viewWidth;

        ViewDescribe(View view, int y) {
            this.view = view;
            this.y = y;
        }
    }
}
