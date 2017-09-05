package com.bian.image.compressor;

import android.content.Context;
import android.widget.Toast;


import java.io.File;
import java.util.List;

class CompressorImpl implements ImageCompressor{

    private ImageOnCompressListener mOnCompressListener;

    private ImageCompressor mImageCompressor;

    CompressorImpl() {
        mImageCompressor = new ZeloryImageCompressor();
    }


    void setOnCompressListener(ImageOnCompressListener onCompressListener) {

        this.mOnCompressListener = onCompressListener;

    }

    @Override
    public void compressImage(Context context, String filePath, ImageOnCompressListener onCompressListener) {

        if (mImageCompressor == null) {
            throw new IllegalArgumentException(
                    "还未设置Compressor");
        }
        File file=new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context,"图片不存在",Toast.LENGTH_SHORT).show();
            return;
        }


        this.mImageCompressor.compressImage(context, filePath, mOnCompressListener);
    }

    @Override
    public void compressImages(Context context, List<String> filePaths, ImageOnCompressListener onCompressListener) {

        if (mImageCompressor == null) {
            throw new IllegalArgumentException(
                    "还未设置Compressor");
        }

        this.mImageCompressor.compressImages(context, filePaths, mOnCompressListener);
    }
}