package com.bian.video.compress;

/**
 * author 边凌
 * date 2017/6/6 14:46
 * desc ${TODO}
 */

public interface OnVideoCompressListener {
    void onCompressStart();

    void onCompressFailed(Throwable throwable);

    void onCompressSuccess(String resultPath);

    void onCompressProgress(String message);
}
