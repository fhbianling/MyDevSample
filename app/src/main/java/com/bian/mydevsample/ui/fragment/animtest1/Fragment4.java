package com.bian.mydevsample.ui.fragment.animtest1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.base.util.utilbase.ScreenUtil;
import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/10/12 22:24
 * 类描述：
 */

public class Fragment4 extends AbsBaseFragment implements View.OnClickListener {
    private final static int[] sIds = new int[]{R.id.v1, R.id.v2, R.id.v3, R.id.v4};
    private final static int[] imgIds = new int[]{R.id.img1, R.id.img2, R.id.img3, R.id.img4};
    private final static int[] sImgRes = new int[]{
            R.mipmap.ic_1,
            R.mipmap.ic_2,
            R.mipmap.ic_3,
            R.mipmap.ic_4,
            R.mipmap.ic_5,
            R.mipmap.ic_6,
            R.mipmap.ic_7,
            R.mipmap.ic_8,
            R.mipmap.ic_9,
    };
    private AnimViewExecutor animViewExecutor;
    private ImageView[] imageViews = new ImageView[imgIds.length];

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_4, container, false);
    }

    @Override
    protected void initView(View rootView) {

        View[] views = new View[sIds.length];
        for (int i = 0; i < sIds.length; i++) {
            views[i] = findViewById(sIds[i]);
            imageViews[i] = (ImageView) findViewById(imgIds[i]);

        }
        animViewExecutor = new AnimViewExecutor(views);
        animViewExecutor.setDstScale(ScreenUtil.getScreenWidth(getContext()) / 2, ScreenUtil
                .getScreenHeight(getContext()) / 2);
        View viewById = findViewById(R.id.start);
        View viewById1 = findViewById(R.id.switchPic);
        if (viewById != null && viewById1 != null) {
            viewById.setOnClickListener(this);
            viewById1.setOnClickListener(this);
        }
    }

    public void start() {
        animViewExecutor.execute();
    }

    public void switchPic() {
        for (ImageView imageView : imageViews) {
            imageView.setImageResource(getImgRes());
        }
    }

    public int getImgRes() {
        int index = (int) (Math.random() * sImgRes.length);
        return sImgRes[index];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                start();
                break;
            case R.id.switchPic:
                switchPic();
                break;
        }
    }
}
