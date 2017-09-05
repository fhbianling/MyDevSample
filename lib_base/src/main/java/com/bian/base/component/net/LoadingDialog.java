package com.bian.base.component.net;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bian.base.R;


/**
 * author 边凌
 * date 2016/6/13 0013 11:17
 * desc ${加载转圈弹窗}
 */
@SuppressWarnings("WeakerAccess")
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    private void initializeView() {
        setContentView(R.layout.loading_dialog_layout);
        ImageView loadingImageView = (ImageView) findViewById(R.id.loading_ImageView);
        loadingImageView.setImageResource(R.drawable.loading_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
        animationDrawable.start();

    }
}
