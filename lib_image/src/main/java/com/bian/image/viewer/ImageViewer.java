package com.bian.image.viewer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bian.image.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * author 边凌
 * date 2017/7/11 15:15
 * 类描述：大图浏览
 */

@SuppressWarnings("SameParameterValue")
public class ImageViewer extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private final static String MULTI = "MULTI";
    private static final String CHECKPOS = "checkPos";
    private PhotoView mPhotoView;
    private ViewPager mViewPager;
    private LinearLayout mIndicator;
    private List<String> filePaths;
    private int checkPos = 0;
    private IndicatorAdapter mIndicatorAdapter;

    /**
     * 打开多图大图浏览界面
     *
     * @param urlOrPaths url集合，可以是url也可以是本地路径，也可以时两者混合
     * @param checkPos   设置默认选中位置，当非0时，界面打开后初始时会自动滚动到选中位置
     */
    private static void start(Context context, List<String> urlOrPaths, int checkPos) {
        Intent starter = new Intent(context, ImageViewer.class);
        starter.putStringArrayListExtra(MULTI, new ArrayList<>(urlOrPaths));
        starter.putExtra(CHECKPOS, checkPos);
        context.startActivity(starter);
    }

    /**
     * 打开单图或多图大图浏览界面
     *
     * @param urlOrPaths url或者path 可变参数
     */
    public static void start(Context context, String... urlOrPaths) {
        start(context, Arrays.asList(urlOrPaths), 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseSetting();
        setContentView(R.layout.activity_imageviewer);
        findView();
        initData();
        parseFilePath();
    }

    private void baseSetting() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initData() {
        checkPos = getIntent().getIntExtra(CHECKPOS, 0);
        filePaths = getIntent().getStringArrayListExtra(MULTI);
    }

    private void parseFilePath() {
        if (filePaths == null || filePaths.size() == 0) return;
        if (filePaths.size() == 1) {
            mPhotoView.setVisibility(View.VISIBLE);
            Glide.with(this).load(filePaths.get(0)).into(mPhotoView);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
            ImageViewerAdapter mAdapter = new ImageViewerAdapter(this, filePaths);
            mViewPager.setAdapter(mAdapter);
            mIndicatorAdapter = new IndicatorAdapter(filePaths.size(), this);
            mViewPager.addOnPageChangeListener(this);
            inflateIndicator();
            mViewPager.setCurrentItem(checkPos);
        }
    }

    private void inflateIndicator() {
        mIndicator.removeAllViews();
        for (int i = 0; i < mIndicatorAdapter.getCount(); i++) {
            mIndicator.addView(mIndicatorAdapter.getView(mIndicator));
        }
        mIndicatorAdapter.setCheckPos(checkPos);
    }

    private void findView() {
        ImageView back = findViewById(R.id.imageViewer_back);
        mPhotoView = findViewById(R.id.imageViewer_pv);
        mViewPager = findViewById(R.id.imageViewer_vp);
        mViewPager.setPageMargin(20);
        mIndicator = findViewById(R.id.imageViewer_indicator);

        back.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mIndicatorAdapter != null) {
            mIndicatorAdapter.setCheckPos(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.imageViewer_back) {
            onBackPressed();
        }
    }

}
