package com.bian.image.selector;

import android.app.Activity;
import android.content.Intent;

import com.bian.image.selector.internal.ImgSelActivity;
import com.bian.image.selector.internal.ImgSelConfig;
import com.bian.image.selector.internal.common.Constant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * author 边凌
 * date 2017/7/2 18:36
 * desc ${TODO}
 */

public class SmuyyhImageSelector implements ImageSelector {
    private static final int DEFAULT_COUNT = 1;
    private final Activity activity;
    private int maxMultiSelectCount = DEFAULT_COUNT;
    private OnResultListener onResultListener;
    private OpenMode openMode;
    private int requestCode;

    SmuyyhImageSelector(Activity activity) {
        this.activity = activity;
    }

    private static <T> List<T> filterSameElem(List<T> list) {
        List<T> result = new ArrayList<>();
        if (list != null) {
            HashSet<T> set = new HashSet<>(list);
            result.addAll(set);
        }
        return result;
    }

    @Override
    public void open(OpenMode openMode, int requestCode, boolean withCrop) {
        this.openMode = openMode;
        this.requestCode = requestCode;
        openMode.setRequestCode(requestCode);
        ImgSelConfig.Builder builder = new ImgSelConfig.Builder(activity,
                                                                new GlideForSmuyyhLoader());
        if (withCrop) {
            builder.needCrop(true);
            builder.cropSize(1, 1, 640, 640);
        }
        builder.rememberSelected(maxMultiSelectCount != 1);
        builder.maxNum(maxMultiSelectCount);
        switch (openMode) {
            case AlbumSingle:
                builder.multiSelect(false);
                builder.needCamera(false);
                break;
            case AlbumMulti:
                builder.multiSelect(true);
                builder.needCamera(false);
                break;
            case Camera:
                builder.needCamera(true);
                builder.multiSelect(true);
                break;
            case Both_AlbumMulti:
                builder.needCamera(true);
                builder.multiSelect(true);
                break;
            case Both_AlbumSingle:
                builder.needCamera(true);
                builder.multiSelect(true);
                break;
        }
        ImgSelActivity.startActivity(activity, builder.build(), requestCode);
    }

    @Override
    public void open(OpenMode openMode, boolean withCrop) {
        open(openMode, openMode.getDefaultRequestCode(), withCrop);
    }

    /**
     * 在记忆选中功能无效的情况下该方法只能控制单次最多选中
     *
     * @param maxMultiSelectCount 最多选中个数
     */
    @Override
    public void setMaxMultiSelectCount(int maxMultiSelectCount) {
        this.maxMultiSelectCount = maxMultiSelectCount;
    }

    /**
     * 注意在该ImageSelector中返回的是所有的结果
     *
     * @param onResultListener {@link ImageSelector.OnResultListener}
     */
    @Override
    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onResultListener == null) {
            return;
        }
        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
            /*注意这里返回的是所有结果*/
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

            onResultListener.onImageSelectResult(filterSameElem(pathList),
                                                 openMode,
                                                 openMode.getRequestCode());
        }
    }

    @Override
    public void filter(List<String> filter) {
        if (filter != null) {
            Constant.imageList = new ArrayList<>(filterSameElem(filter));
        }
    }

    @Override
    public void release() {
        Constant.imageList.clear();
    }

}
