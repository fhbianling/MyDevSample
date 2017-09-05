package com.bian.audio.internal;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * author 边凌
 * date 2017/6/30 12:34
 * 类描述：
 */

public class DefaultAudioRecorderImpl implements AudioRecorder, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private final static String LOG_TAG = "AudioUtil";
    private static final int WHAT_VOLUME = 0;
    private Context context;
    private MediaRecorder mediaRecorder;
    private String resultPath = "";
    private MediaPlayer mediaPlayer;
    private boolean recording;
    private int duration = -1;
    private OnPlayListener onPlayListener;
    private boolean playing = false;
    private Object tag;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what==WHAT_VOLUME){
                if (onPlayListener != null) {
                    onPlayListener.onPlayVolume(msg.arg1);
                }
            }
            return false;
        }
    });
    public DefaultAudioRecorderImpl(Context context) {
        this.context = context;
        resetRecorder();
    }

    private void resetRecorder() {
        recording=false;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        if (duration != -1) {
            mediaRecorder.setMaxDuration(duration);
        }
    }

    @Override
    public void start() throws IOException {
        start(generateDefaultPath());
    }

    @Override
    public void start(String dirPath) throws IOException {
        resetRecorder();
        if (mediaRecorder == null||recording) {
            return;
        }
        try {
            if (!checkFileExist(dirPath)) {
                boolean mkdirs = new File(dirPath).mkdirs();
                if (!mkdirs) {
                    onError("创建缓存目录失败");
                    return;
                }
            }
            File tempFile = new File(dirPath, String.valueOf(System.currentTimeMillis()) + ".amr");
            boolean newFile = tempFile.createNewFile();
            Log.d(LOG_TAG, "create:" + tempFile.getAbsolutePath());

            if (!newFile) {
                onError("音频文件创建失败");
                Log.d(LOG_TAG, "Failed:audio file create failed");
                return;
            }
            resultPath = tempFile.getAbsolutePath();
            mediaRecorder.setOutputFile(resultPath);
            mediaRecorder.prepare();
            Log.d(LOG_TAG, "recording");
            mediaRecorder.start();
            updateVolume();
            recording = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkFileExist(String dirPath) {
        return !TextUtils.isEmpty(resultPath) && new File(resultPath).exists();
    }

    private void updateVolume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (recording){
                    try {
                        /*每100ms更新一次音量数据*/
                        Thread.sleep(100);
                        if (mediaRecorder == null) {
                            return;
                        }
                        int maxAmplitude = mediaRecorder.getMaxAmplitude();
                        Message message=handler.obtainMessage();
                        message.what= WHAT_VOLUME;
                        message.arg1=maxAmplitude;
                        handler.sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void onError(String msg) {
        Log.d(LOG_TAG,msg);
    }

    @Override
    public boolean stop() {
        if (mediaRecorder == null || !recording) {
            return false;
        }

        try {
            Log.d(LOG_TAG, "stop");
            mediaRecorder.stop();
            recording=false;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                    mediaRecorder=null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return new File(getResultPath()).exists();
    }

    @Override
    public String getResultPath() {
        return resultPath;
    }

    @Override
    public void play(String filePath) throws IOException {
        initMediaPlayer();
        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startInternal();
                }
            });
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(LOG_TAG, "playFailed:" + e.getMessage());
        }
    }

    @Override
    public void play(String filePath, Object tag) throws IOException {
        play(filePath);
        this.tag = tag;
    }

    private void startInternal() {
        if (onPlayListener != null) {
            onPlayListener.onPlayStart(tag);
        }
        Log.d(LOG_TAG, "onPlayStart");
        playing = true;
        mediaPlayer.start();
    }

    @Override
    public void play(Uri uri) throws IOException {
        initMediaPlayer();
        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    startInternal();
                }
            });
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(LOG_TAG, "playFailed:" + e.getMessage());
        }
    }

    @Override
    public void play(Uri uri, Object tag) throws IOException {
        this.tag = tag;
        play(uri);
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    @Override
    public void getAudioDurationAsync(String audioPath, final OnDurationListener onDurationListener) {
        initMediaPlayer();
        if (onDurationListener == null) {
            return;
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onDurationListener.onDurationCalcComplete(mediaPlayer.getDuration());
                mediaPlayer.release();
            }
        });
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer.release();
        }
    }

    @Override
    public int getAudioDuration(String audioPath) {
        initMediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.release();
        }
        return 0;
    }

    @Override
    public void setRecordMaxLength(int duration) {
        this.duration = duration;
    }

    @Override
    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    private void initMediaPlayer() {
        if (playing && onPlayListener != null) {
            onPlayListener.onPlayError(tag);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    private String generateDefaultPath() {
        String audioPath = String.valueOf(System.currentTimeMillis()) + ".aac";
        String rootPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "AudioCache";
        return rootPath + File.separator + audioPath;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(LOG_TAG, "onPlayError");
        if (onPlayListener != null) {
            onPlayListener.onPlayError(tag);
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPlayCompletion");
        if (onPlayListener != null) {
            onPlayListener.onPlayCompletion(tag);
        }
    }

}
