package com.bian.audio.internal;

import android.net.Uri;
import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * author 边凌
 * date 2017/6/30 11:49
 * 类描述：
 */

public interface AudioRecorder {
    void start() throws IOException;
    void start(String filePath) throws IOException;
    boolean stop();
    String getResultPath();
    void play(String filePath) throws IOException;
    void play(String filePath,Object tag) throws IOException;
    void play(Uri uri) throws IOException;
    void play(Uri uri,Object tag) throws IOException;
    void release();
    /*返回毫秒*/
    void getAudioDurationAsync(String audioPath, OnDurationListener onDurationListener);

    int getAudioDuration(String audioPath);

    /*单位毫秒*/
    void setRecordMaxLength(int duration);

    void setOnPlayListener(OnPlayListener onPlayListener);

    Object getTag();

    interface OnPlayListener{
        void onPlayStart(@Nullable Object tag);
        void onPlayCompletion(@Nullable Object tag);
        void onPlayError(@Nullable Object tag);
        void onPlayVolume(int volume);
    }

    interface OnDurationListener{
        void onDurationCalcComplete(int duration);
    }
}
