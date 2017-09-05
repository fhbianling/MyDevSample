package com.bian.audio;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bian.audio.internal.AudioRecorder;
import com.bian.audio.internal.DefaultAudioRecorderImpl;

import java.io.IOException;
import java.util.HashSet;


/**
 * author 边凌
 * date 2017/6/30 11:48
 * 类描述：音频的相关工具类
 */

public class AudioUtil implements AudioRecorder, AudioRecorder.OnPlayListener {
    public final static String[] PERMISSION_AUDIO;
    private final static String LOG_TAG="lib_media";
    static {
        PERMISSION_AUDIO = new String[2];
        PERMISSION_AUDIO[0] = Manifest.permission.RECORD_AUDIO;
        PERMISSION_AUDIO[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    }

    private AudioRecorder audioRecorder;

    private static AudioUtil sInstance;
    private AudioUtil(Context context){
        audioRecorder = new DefaultAudioRecorderImpl(context);
        audioRecorder.setOnPlayListener(this);
    }

    public static AudioUtil getInstance(Context context){
        if(sInstance==null){
            synchronized(AudioUtil.class){
                if(sInstance==null){
                    sInstance=new AudioUtil(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public DefaultAudioRecorderImpl newRecorder(Context context){
        return new DefaultAudioRecorderImpl(context);
    }

    private boolean start;
    @Override
    public void start() throws IOException {
        Log.d(LOG_TAG,"startAudioRecord");
        audioRecorder.start();
    }

    @Override
    public void start(String filePath) throws IOException {
        Log.d(LOG_TAG,"startAudioRecord:"+filePath);
        audioRecorder.start(filePath);
        start=true;
    }

    @Override
    public boolean stop() {
        if (!start){
            return false;
        }
        start=false;
        Log.d(LOG_TAG,"stopAudioRecord");
        return audioRecorder.stop();
    }

    @Override
    public String getResultPath() {
        return audioRecorder.getResultPath();
    }

    @Override
    public void play(String filePath) throws IOException {
        Log.d(LOG_TAG,"playAudio:"+filePath);
        audioRecorder.play(filePath);
    }

    @Override
    public void play(String filePath, Object tag) throws IOException {
        audioRecorder.play(filePath, tag);
    }

    @Override
    public void play(Uri uri) throws IOException {
        Log.d(LOG_TAG,"playAudio:"+uri);
        audioRecorder.play(uri);
    }

    @Override
    public void play(Uri uri, Object tag) throws IOException {
        audioRecorder.play(uri,tag);
    }

    @Override
    public void release() {
        clearListeners();
        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    @Override
    public void getAudioDurationAsync(String audioPath, OnDurationListener onDurationListener) {
        audioRecorder.getAudioDurationAsync(audioPath,onDurationListener);
    }

    @Override
    public int getAudioDuration(String audioPath) {
        return audioRecorder.getAudioDuration(audioPath);
    }

    @Override
    public void setRecordMaxLength(int duration) {
        audioRecorder.setRecordMaxLength(duration);
    }

    private HashSet<OnPlayListener> onPlayListeners=new HashSet<>();
    @Override
    public void setOnPlayListener(OnPlayListener onPlayListener) {
        onPlayListeners.add(onPlayListener);
    }

    @Override
    public Object getTag() {
        return audioRecorder.getTag();
    }

    private void clearListeners(){
        for (OnPlayListener onPlayListener : onPlayListeners) {
            onPlayListener.onPlayCompletion(getTag());
        }
        onPlayListeners.clear();
    }

    @Override
    public void onPlayStart(@Nullable Object tag) {
        for (OnPlayListener onPlayListener : onPlayListeners) {
            onPlayListener.onPlayStart(tag);
        }
    }

    @Override
    public void onPlayCompletion(@Nullable Object tag) {
        for (OnPlayListener onPlayListener : onPlayListeners) {
            onPlayListener.onPlayCompletion(tag);
        }
    }

    @Override
    public void onPlayError(@Nullable Object tag) {
        for (OnPlayListener onPlayListener : onPlayListeners) {
            onPlayListener.onPlayError(tag);
        }
    }

    @Override
    public void onPlayVolume(int volume) {
        for (OnPlayListener onPlayListener : onPlayListeners) {
            onPlayListener.onPlayVolume(volume);
        }
    }
}
