package com.bian.image.compressor;

import android.content.Context;

import java.util.List;

interface ImageCompressor {

    void compressImage(Context context, String filePath, ImageOnCompressListener onCompressListener);

    void compressImages(Context context, List<String> filePaths, ImageOnCompressListener onCompressListener);
}
