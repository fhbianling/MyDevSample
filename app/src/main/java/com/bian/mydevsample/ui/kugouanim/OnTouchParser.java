package com.bian.mydevsample.ui.kugouanim;

import android.view.MotionEvent;
import android.view.View;

/**
 * author 边凌
 * date 2017/12/23 13:11
 * 类描述：
 */

public class OnTouchParser {

    OnTouchParser(OnMoveListener mOnMoveListener) {
        this.mOnMoveListener = mOnMoveListener;
    }

    private float lastX;
    private float startMoveX;

    boolean parse(View v, MotionEvent event) {
        int action = event.getAction();
        float nowX = event.getRawX();
        float moveLength = Math.abs(nowX - startMoveX);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = nowX;
                startMoveX = nowX;
                break;
            case MotionEvent.ACTION_UP:
                lastX = 0;
                startMoveX = 0;
                return mOnMoveListener.onActionUp(v, event, moveLength);
            case MotionEvent.ACTION_MOVE:
                boolean moveRight = nowX - lastX > 0;
                boolean result;
                if (moveRight) {
                    result = mOnMoveListener.onMoveRight(v, event, moveLength);
                } else {
                    result = mOnMoveListener.onMoveLeft(v, event, moveLength);
                }
                return result;
        }
        return mOnMoveListener.handle(v, event, moveLength);
    }


    private OnMoveListener mOnMoveListener;

    public interface OnMoveListener {
        boolean onMoveLeft(View v, MotionEvent event, float moveLength);

        boolean onMoveRight(View v, MotionEvent event, float moveLength);

        boolean handle(View v, MotionEvent event, float moveLength);

        boolean onActionUp(View v, MotionEvent event, float lastMoveLength);
    }
}
