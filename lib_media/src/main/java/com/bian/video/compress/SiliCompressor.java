package com.bian.video.compress;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.net.URISyntaxException;

import com.bian.video.VideoInfo;

/**
 * author 边凌
 * date 2017/6/6 17:21
 * desc ${TODO}
 */

class SiliCompressor implements VideoCompressor {
    private final static String LOG_TAG = "lib_media";
    private final static int COMPRESS_SUCCESS = 0x21;
    private static final int COMPRESS_FAILED = 0x22;
    private Context context;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (onVideoCompressListener == null) {
                return false;
            }
            if (msg.what == COMPRESS_SUCCESS) {
                onVideoCompressListener.onCompressSuccess(resultPath);
            } else if (msg.what == COMPRESS_FAILED) {
                onVideoCompressListener.onCompressFailed(new CompressException("压缩失败"));
            }

            return false;
        }
    });
    private String resultDir;
    private String resultPath;
    private OnVideoCompressListener onVideoCompressListener;

    private SiliCompressor(Context context) {
        this.context = context.getApplicationContext();
    }

    @TargetApi(16)
    static SiliCompressor getInstance(Context context) {
        return new SiliCompressor(context);
    }

    @Override
    public void compress(VideoInfo videoInfo, String dstPath, OnVideoCompressListener onVideoCompressListener) {
        this.resultDir = dstPath;
        this.onVideoCompressListener = onVideoCompressListener;
        onVideoCompressListener.onCompressStart();
        String originalPath = videoInfo.getFilePath();

        File file = new File(originalPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String uriString = Uri.fromFile(file).toString();
        final String fileName = file.getName();
        Log.d(LOG_TAG, "startCompress:\n\t\t[fileName:" + fileName + ",originalPath:" + originalPath + "]");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String compressVideoPath = com.iceteck.silicompressorr.SiliCompressor.
                            with(context).
                            compressVideo(uriString, resultDir);
                    File file = new File(compressVideoPath);
                    Log.d(LOG_TAG, "endCompress:\n\t\t[fileName:" + fileName + ",resultPath:" + compressVideoPath + "]");
                    if (file.exists()) {
                        resultPath = compressVideoPath;
                        handler.sendEmptyMessage(COMPRESS_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(COMPRESS_FAILED);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
