package com.bian.mydevsample.ui.kugouanim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bian.util.core.ScreenUtil;
import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/12/23 14:19
 * 类描述：
 */

public class SongLrcView extends FrameLayout implements OnTouchParser.OnMoveListener,
                                                        ValueAnimator.AnimatorUpdateListener {
    private final static int ROTATION_OUT_SCREEN = 30;
    private final static int DURATION = 300;
    private ImageView bg;
    private LinearLayout lrcRoot;
    private SongInfoView songInfoView;
    private int screenWidth;
    private final OnTouchParser onTouchParser;
    private ValueAnimator.AnimatorUpdateListener rotationListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            onRotationChange();
        }
    };

    public SongLrcView(@NonNull Context context,
                       @Nullable AttributeSet attrs) {
        super(context, attrs);
        addView(LayoutInflater.from(context).inflate(R.layout.activity_kugou_play, this, false));
        bg = findViewById(R.id.bg);
        lrcRoot = findViewById(R.id.lrcRoot);
        songInfoView = findViewById(R.id.songInfoView);
        screenWidth = ScreenUtil.getScreenWidth(context);
        songInfoView.setX(screenWidth);
        songInfoView.setVisibility(View.VISIBLE);
        onTouchParser = new OnTouchParser(this);
        findViewById(R.id.imgb).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        setVisibility(GONE);
    }

    public void init() {
        setPivotX(screenWidth * 4 / 3);
        setPivotY(ScreenUtil.getScreenHeight(getContext()) * 3);
        setVisibility(VISIBLE);
        setRotation(ROTATION_OUT_SCREEN);
    }

    public void moveIn() {
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(this,
                                                             "rotation",
                                                             ROTATION_OUT_SCREEN,
                                                             0);
        rotationAnim.addUpdateListener(rotationListener);
        rotationAnim.setDuration(DURATION);
        rotationAnim.start();
    }

    private void back() {
        rotationMoveOut();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchParser.parse(this, event);
    }

    @Override
    public boolean onMoveLeft(View v, MotionEvent event, float moveLength) {
        float x = songInfoView.getX();
        if (x == 0) return true;
        songInfoView.setX(screenWidth * (1 - moveLength / screenWidth));
        traceAlphaOfLrc();
        return false;
    }

    @Override
    public boolean onMoveRight(View v, MotionEvent event, float moveLength) {
        float x = songInfoView.getX();
        if (x >= screenWidth) {
            float rotation = (moveLength / screenWidth) * ROTATION_OUT_SCREEN;
            setRotation(rotation);
            onRotationChange();
            return true;
        }
        songInfoView.setX(screenWidth * (moveLength / screenWidth));
        traceAlphaOfLrc();
        return false;
    }

    @Override
    public boolean handle(View v, MotionEvent event, float moveLength) {
        return true;
    }

    @Override
    public boolean onActionUp(View v, MotionEvent event, float lastMoveLength) {
        float rotation = getRotation();
        if (rotation >= ROTATION_OUT_SCREEN) {
            return false;
        } else if (rotation > ROTATION_OUT_SCREEN / 2) {
            rotationMoveOut();
            return false;
        } else if (rotation > 0) {
            rotationMoveIn();
            return false;
        }

        float x = songInfoView.getX();
        boolean atLeftScreen = x < screenWidth / 2;
        if (atLeftScreen) {
            infoViewMoveFront();
        } else {
            infoViewMoveBack();
        }
        return false;
    }

    private void rotationMoveIn() {
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(this, "rotation", getRotation(), 0);
        rotationAnim.setDuration(DURATION);
        rotationAnim.addUpdateListener(rotationListener);
        rotationAnim.start();
    }

    private void rotationMoveOut() {
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(this,
                                                             "rotation",
                                                             getRotation(),
                                                             ROTATION_OUT_SCREEN);
        rotationAnim.addUpdateListener(rotationListener);
        rotationAnim.setDuration(DURATION);
        rotationAnim.start();
    }

    private void infoViewMoveBack() {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(songInfoView,
                                                          "x",
                                                          songInfoView.getX(),
                                                          screenWidth);
        xAnimator.addUpdateListener(this);
        xAnimator.setDuration(DURATION);
        xAnimator.start();
    }

    private void infoViewMoveFront() {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(songInfoView,
                                                          "x",
                                                          songInfoView.getX(),
                                                          0);
        xAnimator.addUpdateListener(this);
        xAnimator.setDuration(DURATION);
        xAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        traceAlphaOfLrc();
    }

    private void traceAlphaOfLrc() {
        float x = songInfoView.getX();
        float alpha = x / screenWidth;
        lrcRoot.setAlpha(alpha);
    }


    private void onRotationChange() {
        mOnEnterListener.onEnterExit(getRotation() / ROTATION_OUT_SCREEN);
    }

    private OnEnterListener mOnEnterListener;

    public void setOnEnterExitListener(OnEnterListener value) {
        this.mOnEnterListener = value;
    }

    public interface OnEnterListener {
        void onEnterExit(float rate);
    }
}
