package com.bian.mydevsample.ui.pageanim4;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bian.widget.SimpleAnimListener;
import com.bian.image.loader.GlideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/12/6 15:47
 * 类描述：
 */

public class ImageGallery extends FrameLayout {
    public static final String PROPERTY_NAME = "alpha";
    public static final int ANIM_DURATION = 2000;
    public static final int RETENTION_DELAY = 5000;
    private static final int UPDATE = 0x12;
    private ImageView[] ivs = new ImageView[2];
    private int frontIndex;
    private List<String> imgUrls;
    private View inFrontView;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == UPDATE) {
                if (imgUrls == null) {
                    handler.removeMessages(UPDATE);
                    return false;
                }
                if (imgUrls.size() == 1) {
                    frontIndex = 0;
                    inflateImage(frontIndex);
                    handler.removeMessages(UPDATE);
                    return false;
                }
                inflateImage(frontIndex);
                frontIndex++;
                if (frontIndex >= imgUrls.size()) {
                    frontIndex = 0;
                }
                handler.sendEmptyMessageDelayed(UPDATE, RETENTION_DELAY);
            }

            return false;
        }
    });

    public ImageGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i < ivs.length; i++) {
            ivs[i] = new ImageView(context);
            ivs[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivs[i].setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                                                                LayoutParams.MATCH_PARENT));
        }
        imgUrls = getMockDatas();
        handler.sendEmptyMessage(UPDATE);
    }

    private List<String> getMockDatas() {
        List<String> mockDatas = new ArrayList<>();
        mockDatas.add("http://img1.gamersky.com/image2017/11/20171126_syj_380_14/image001.jpg");
        mockDatas.add("http://img1.gamersky.com/image2017/11/20171126_syj_380_14/image005.jpg");
        mockDatas.add("http://img1.gamersky.com/image2017/11/20171126_syj_380_14/image007.jpg");
        mockDatas.add("http://img1.gamersky.com/image2017/11/20171126_syj_380_14/image015.jpg");
        return mockDatas;
    }

    private void inflateImage(int frontIndex) {
        if (frontIndex == 0 && imgUrls.size() == 1) {
            removeAllViews();
            addView(ivs[0]);
            GlideUtil.load(ivs[0], imgUrls.get(0));
            handler.removeMessages(UPDATE);
            return;
        }

        if (getChildCount() == 0) {
            addView(ivs[0]);
            GlideUtil.load(ivs[0], imgUrls.get(0));
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivs[0], PROPERTY_NAME, 0f, 1f);
            objectAnimator.setDuration(ANIM_DURATION);
            objectAnimator.start();
            return;
        }

        for (ImageView iv : ivs) {
            if (indexOfChild(iv) >= 0) {
                inFrontView = iv;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv, PROPERTY_NAME, 1f, 0);
                objectAnimator.setDuration(ANIM_DURATION);
                objectAnimator.start();
                objectAnimator.addListener(new SimpleAnimListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeView(inFrontView);
                    }
                });
            } else {
                addView(iv);
                GlideUtil.load(iv, imgUrls.get(frontIndex));
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv,
                                                                       PROPERTY_NAME,
                                                                       0f,
                                                                       1f);
                objectAnimator.setDuration(ANIM_DURATION);
                objectAnimator.start();
            }
        }

    }

}
