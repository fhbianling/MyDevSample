package com.bian.video.compress;

import com.bian.video.VideoInfo;

/**
 * author 边凌
 * date 2017/6/6 16:29
 * desc ${TODO}
 */

interface VideoCompressor {
    void compress(final VideoInfo videoInfo, final String resultPath, final OnVideoCompressListener onVideoCompressListener);
}
