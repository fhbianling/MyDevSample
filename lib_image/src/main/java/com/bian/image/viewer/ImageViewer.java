package com.bian.image.viewer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
 * 类描述：
 */

public class ImageViewer extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private final static String MULTI = "MULTI";
    private static final String CHECKPOS = "checkPos";
    private PhotoView mPhotoView;
    private ViewPager mViewPager;
    private ImageView back;
    private LinearLayout mIndicator;
    private List<String> filePaths;
    private int checkPos = 0;
    private IndicatorAdapter mIndicatorAdapter;

    public static void start(Context context, List<String> urlOrPaths, int checkPos) {
        Intent starter = new Intent(context, ImageViewer.class);
        starter.putStringArrayListExtra(MULTI, new ArrayList<>(urlOrPaths));
        starter.putExtra(CHECKPOS, checkPos);
        context.startActivity(starter);
    }

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
        back = (ImageView) findViewById(R.id.imageViewer_back);
        mPhotoView = (PhotoView) findViewById(R.id.imageViewer_pv);
        mViewPager = (ViewPager) findViewById(R.id.imageViewer_vp);
        mViewPager.setPageMargin(20);
        mIndicator = (LinearLayout) findViewById(R.id.imageViewer_indicator);

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
