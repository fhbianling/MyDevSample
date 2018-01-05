package com.bian.mydevsample.ui.kugouanim;

import android.os.Bundle;
import android.view.View;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/12/23 11:39
 * 类描述：
 */

public class KuGouMainAnimActivity extends BaseActivity implements SongLrcView.OnEnterListener {
    private SongLrcView songLrcView;
    private View content;

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_kugouanim;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        songLrcView = findViewById(R.id.songLrcView);
        content = findViewById(R.id.content);
        songLrcView.init();
        songLrcView.setOnEnterExitListener(this);
    }

    public void openPlayPage(View view) {
        songLrcView.moveIn();
    }

    @Override
    public void onEnterExit(float rate) {
        float scale = (float) (0.8 + 0.2 * rate);
        content.setScaleX(scale);
        content.setScaleY(scale);
    }
}
