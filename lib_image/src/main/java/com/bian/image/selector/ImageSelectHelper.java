package com.bian.image.selector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import java.util.List;

/**
 * author 边凌
 * date 2017/3/17 10:44
 * desc ${图片选择工具类}
 */

@SuppressWarnings("unused")
public class ImageSelectHelper implements ImageSelector {
    public static final String[] PERMISSION = new String[4];
    public static String FILE_PROVIDER_PATH;

    static {
        PERMISSION[0] = Manifest.permission.CAMERA;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PERMISSION[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
        } else {
            PERMISSION[1] = "";
        }
        PERMISSION[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        PERMISSION[3] = Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS;
    }

    private ImageSelector imageSelector;
    private Activity activity;

    public ImageSelectHelper(Activity activity) {
        this.activity = activity;
        imageSelector = getImageSelector();
    }

    private ImageSelector getImageSelector() {
        return new SmuyyhImageSelector(activity);
    }

    @Override
    public void open(OpenMode openMode, int requestCode, boolean withCrop) {
        imageSelector.open(openMode, requestCode, withCrop);
    }

    @Override
    public void open(OpenMode openMode, boolean withCrop) {
        imageSelector.open(openMode, withCrop);
    }

    @Override
    public void setMaxMultiSelectCount(int maxMultiSelectCount) {
        imageSelector.setMaxMultiSelectCount(maxMultiSelectCount);
    }

    @Override
    public void setOnResultListener(OnResultListener onResultListener) {
        imageSelector.setOnResultListener(onResultListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void filter(List<String> filter) {
        imageSelector.filter(filter);
    }

    @Override
    public void release() {
        imageSelector.release();
    }

    public static void setFileProviderPath(String sFileProviderPath){
        ImageSelectHelper.FILE_PROVIDER_PATH = sFileProviderPath;
    }

}
