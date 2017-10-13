package com.bian.mydevsample.ui.fragment.floatinganim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * author 边凌
 * date 2017/10/13 11:15
 * 类描述：
 */

// fixme: 2017/10/13 当执行动画的队列中包含的view高度不等时，显示会出现重叠的现象
public class FloatingAnimSubmitter {
    /**
     * 单条动画的持续时间
     */
    public static final int DURATION = 3000;
    /**
     * 同时显示的浮动条槽位数
     */
    private final static int COUNT = 3;
    private Activity activity;
    private FloatingAnimSlotExecutor[] floatingAnimSlotExecutor = new FloatingAnimSlotExecutor[COUNT];
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
     * 执行动画，这里只关注view动画,view动画的点击事件应该在外面写
     *
     * @param view 要执行动画的view
     */
    public void execute(View view) {
        int viewAnimSlotIndex = calcViewAnimSlotIndex();
        int heightValue = calcViewAnimHeight(viewAnimSlotIndex);

        if (indexValid(viewAnimSlotIndex)) {
            floatingAnimSlotExecutor[viewAnimSlotIndex].execute(view, heightValue);
        }
    }

    /**
     * 验证槽位序号有效
     */
    private boolean indexValid(int viewAnimSlotIndex) {
        return viewAnimSlotIndex < floatingAnimSlotExecutor.length;
    }

    /**
     * 计算view应该被排入的槽位的序号
     */
    private int calcViewAnimSlotIndex() {
        int result = 0;
        for (int i = 0; i < floatingAnimSlotExecutor.length; i++) {
            initIfNecessary(i);
            if (floatingAnimSlotExecutor[result].getViewCount() > floatingAnimSlotExecutor[i]
                    .getViewCount()) {
                result = i;
            }
            heightValue[i] = floatingAnimSlotExecutor[i].getHeightOfView();
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

    /**
     * 考虑到当view点击事件发生时可能会打开新界面，
     * 该方法暂停当前所有动画
     */
    public void setPause() {
        pause = true;
        for (FloatingAnimSlotExecutor animSlotExecutor : floatingAnimSlotExecutor) {
            if (animSlotExecutor != null) {
                animSlotExecutor.setPause();
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
     * 该方法恢复当前所有动画
     */
    public void setResume() {
        pause = false;
        for (FloatingAnimSlotExecutor animSlotExecutor : floatingAnimSlotExecutor) {
            if (animSlotExecutor != null) {
                animSlotExecutor.setResume();
            }
        }
    }

    private void initIfNecessary(int i) {
        if (floatingAnimSlotExecutor[i] == null) {
            floatingAnimSlotExecutor[i] = new FloatingAnimSlotExecutor(activity);
        }
    }

    /**
     * author 边凌
     * date 2017/10/13 10:17
     * 类描述：单个浮动条槽位,描述该槽位中的view集合的动画执行逻辑
     */

    private static class FloatingAnimSlotExecutor implements Animator.AnimatorListener {
        private static int sStartX;
        private ViewGroup decorView;
        private Set<ViewDescribe> viewSet = new LinkedHashSet<>();
        private ValueAnimator valueAnimator;
        private ViewDescribe viewDescribe;

        FloatingAnimSlotExecutor(Activity activity) {
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
            if (valueAnimator == null || !valueAnimator.isStarted()) {
                addToParentAndStartAnim(viewDescribe);
            }
        }

        int getHeightOfView() {
            if (viewSet.iterator().hasNext()) {
                View view = viewSet.iterator().next().view;
                view.measure(0, 0);
                return view.getMeasuredHeight();
            }
            return 0;
        }

        int getViewCount() {
            return viewSet.size();
        }

        /**
         * 该方法中在view
         *
         * @param viewDescribe
         */
        private void addToParentAndStartAnim(final ViewDescribe viewDescribe) {
            View view = viewDescribe.view;
            decorView.addView(view);
            startAnim(viewDescribe);
        }

        private void startAnim(ViewDescribe viewDescribe) {
            this.viewDescribe = viewDescribe;
            final View view = viewDescribe.view;
            int yOfView = viewDescribe.yOfView;
            view.setY(yOfView);
            if (sStartX == 0) {
                sStartX = view.getContext().getResources().getDisplayMetrics().widthPixels;
            }
            view.measure(0, 0);
            int endX = -view.getMeasuredWidth();

            valueAnimator = ObjectAnimator.ofInt(sStartX, endX);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int x = (int) animation.getAnimatedValue();
                    view.setX(x);
                }
            });
            valueAnimator.addListener(this);
            valueAnimator.setDuration(DURATION);
            valueAnimator.start();
        }

        private void executeRemain() {
            Iterator<ViewDescribe> iterator = viewSet.iterator();
            if (iterator.hasNext()) {
                addToParentAndStartAnim(iterator.next());
            }
        }

        private void setPause() {
            if (valueAnimator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    valueAnimator.pause();
                }
            }
        }

        private void setResume() {
            if (valueAnimator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    valueAnimator.resume();
                }
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
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
     * 描述view及其执行动画时应该处于的y坐标
     */
    private static class ViewDescribe {
        private View view;
        private int yOfView;

        ViewDescribe(View view, int yOfView) {
            this.view = view;
            this.yOfView = yOfView;
        }
    }
}
