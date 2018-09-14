package com.bian.mydevsample.ui.infotextview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bian.util.core.Checker;
import com.bian.util.core.ToastUtil;
import com.bian.widget.infotextview.InfoTextView;
import com.bian.image.loader.GlideUtil;
import com.bian.image.selector.ImageSelectHelper;
import com.bian.image.selector.ImageSelector;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.List;

/**
 * author 边凌
 * date 2017/11/30 10:28
 * 类描述：
 */

public class InfoTextViewSampleActivity extends BaseActivity
        implements ImageSelector.OnResultListener, InfoTextView.OnInfoViewClickListener,
                   InfoTextView.OnDrawableClickListener, InfoTextView.OnCheckedChangeListener {
    private ImageSelectHelper selectHelper;
    private InfoTextView avatar;

    public static void start(Context context) {
        Intent starter = new Intent(context, InfoTextViewSampleActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_info_text_view_sample;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        avatar = findViewById(R.id.avatar);
        View infoView = avatar.getInfoView();
        if (infoView instanceof ImageView) {
            GlideUtil.loadCircle((ImageView) infoView, R.mipmap.avatar_test);
        }
        avatar.setOnInfoViewClickListener(this);
        InfoTextView infoTextView = findViewById(R.id.drawableClick);
        infoTextView.setOnDrawableClickListener(this);
        InfoTextView checkable = findViewById(R.id.checkable);
        checkable.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageSelectResult(List<String> imageResult, ImageSelector.OpenMode openMode,
                                    int requestCode) {
        if (!Checker.isEmpty(imageResult)) {
            String resultPath = imageResult.get(0);
            View infoView = avatar.getInfoView();
            if (infoView instanceof ImageView) {
                GlideUtil.loadCircle((ImageView) infoView, resultPath);
            }
        }
    }

    @Override
    public void onImageSelectError(String msg) {

    }

    @Override
    public void onClick(InfoTextView infoTextView, View infoView) {
        if (selectHelper == null) {
            selectHelper = new ImageSelectHelper(this);
            selectHelper.setOnResultListener(this);
        }
        selectHelper.open(ImageSelector.OpenMode.AlbumSingle, false);
    }

    @Override
    public void onDrawableLeftClick(InfoTextView infoTextView, View drawableLeft) {

    }

    @Override
    public void onDrawableRightClick(InfoTextView infoTextView, View drawableRight) {
        ToastUtil.showToastShort("点击了右侧drawable,info text:" + infoTextView.getInfoText());
    }

    @Override
    public void onCheckedChange(InfoTextView infoTextView, boolean isCheck) {
        ToastUtil.showToastShort("on checked change,isCheck:" + isCheck);
    }
}
