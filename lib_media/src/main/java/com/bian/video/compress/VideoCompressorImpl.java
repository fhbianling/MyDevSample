package com.bian.video.compress;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bian.video.VideoInfo;

/**
 * author 边凌
 * date 2017/6/6 14:24
 * desc ${TODO}
 */

public class VideoCompressorImpl implements VideoCompressor {
    @SuppressLint("StaticFieldLeak")
    private static volatile VideoCompressorImpl sInstance;
    private VideoCompressor videoCompressor;

    private VideoCompressorImpl(Context context) {
        videoCompressor = createCompressor(context);
    }

    public static VideoCompressorImpl getCompressor(Context context) {
        if (sInstance == null) {
            synchronized (VideoCompressorImpl.class) {
                if (sInstance == null) {
                    sInstance = new VideoCompressorImpl(context);
                }
            }
        }
        return sInstance;
    }

    private VideoCompressor createCompressor(Context context) {
        return SiliCompressor.getInstance(context);
    }

    private Object readResolve() {
        return sInstance;
    }

    @Override
    public void compress(final VideoInfo videoInfo, final String resultPath, final OnVideoCompressListener onVideoCompressListener) {
        videoCompressor.compress(videoInfo, resultPath, onVideoCompressListener);
    }

}
