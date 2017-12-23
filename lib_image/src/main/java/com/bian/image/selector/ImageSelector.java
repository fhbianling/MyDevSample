package com.bian.image.selector;

import android.content.Intent;

import java.util.List;

/**
 * author 边凌
 * date 2017/7/2 17:05
 * desc ${TODO}
 */

public interface ImageSelector {
    void open(OpenMode openMode, int requestCode, boolean withCrop);

    void open(OpenMode openMode, boolean withCrop);

    void setMaxMultiSelectCount(int maxMultiSelectCount);

    void setOnResultListener(OnResultListener onResultListener);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void filter(List<String> filter);

    void release();

    enum OpenMode {
        /*相册单选*/
        AlbumSingle(0x11),
        /*相册多选*/
        AlbumMulti(0x12),
        /*拍照*/
        Camera(0x13),
        /*拍照+相册单选*/
        Both_AlbumSingle(0x14),
        /*拍照+相册多选*/
        Both_AlbumMulti(0x15);
        private int requestCode;
        private final int defaultRequestCode;

        OpenMode(int requestCode) {
            this.requestCode = requestCode;
            this.defaultRequestCode = requestCode;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getDefaultRequestCode() {
            return defaultRequestCode;
        }
    }

    interface OnResultListener {
        void onImageSelectResult(List<String> imageResult, OpenMode openMode, int requestCode);

        void onImageSelectError(String msg);
    }

}
