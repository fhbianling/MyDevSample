package com.bian.mydevsample.ui.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bian.base.util.utilbase.L;
import com.bian.mydevsample.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * author 边凌
 * date 2017/10/13 15:30
 * 类描述：
 */

public class MediaPlayerView extends RelativeLayout implements View.OnClickListener,
                                                               MediaPlayer.OnPreparedListener,
                                                               MediaPlayer.OnCompletionListener,
                                                               SeekBar.OnSeekBarChangeListener,
                                                               MediaPlayer.OnErrorListener {

    public final static int NO_LAST = 0X11;
    public final static int NO_NEXT = 0X12;
    private TextView curTime;
    private SeekBar seek;
    private TextView totalTime;
    private ImageView pause;
    private OnMediaPlayListener mOnMediaPlayListener;
    private OnPlayerExtListenerExtListener mOnPlayerExtListenerExtListener;
    private List<DataSource> sources = new ArrayList<>();
    private int currIndex;
    private MediaPlayer mediaPlayer;
    private boolean prepared;
    private Timer timer = new Timer();
    private int duration;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if (!mediaPlayer.isPlaying()) currentPosition = duration;

                seek.setProgress(currentPosition);
                curTime.setText(MediaPlayerHelper.formatTime(currentPosition));
                if (mOnMediaPlayListener != null) {
                    mOnMediaPlayListener.onPlayProgress(sources.get(currIndex),
                                                        currentPosition,
                                                        duration);
                }
            }
            return false;
        }
    });
    private int seekToProgress;
    private OnMediaPlayPrepareListener mOnMediaPlayPrepareListener;

    public MediaPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_mediaplayer, this, true);
        curTime = (TextView) findViewById(R.id.cur_time);
        seek = (SeekBar) findViewById(R.id.seek);
        totalTime = (TextView) findViewById(R.id.totalTime);
        pause = (ImageView) findViewById(R.id.pause);

        findViewById(R.id.collect).setOnClickListener(this);
        findViewById(R.id.last).setOnClickListener(this);
        pause.setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                playNext();
                break;
            case R.id.last:
                playLast();
                break;
            case R.id.pause:
                playPauseOrResume();
                break;
            case R.id.more:
                onClickMore();
                break;
            case R.id.collect:
                onClickCollect();
                break;
        }
    }

    private void onClickCollect() {
        if (mOnPlayerExtListenerExtListener != null) {
            mOnPlayerExtListenerExtListener.onCollect();
        }
    }

    private void onClickMore() {
        if (mOnPlayerExtListenerExtListener != null) {
            mOnPlayerExtListenerExtListener.onMore();
        }
    }

    private void playPauseOrResume() {
        if (mediaPlayer.isPlaying()) {
            pause();
        } else {
            startOrResume();
        }
    }

    private void pause() {
        if (mOnMediaPlayListener != null) {
            mOnMediaPlayListener.onPlayPause(sources.get(currIndex));
        }
        pause.setImageResource(R.mipmap.ic_media_resume);
        mediaPlayer.pause();
    }

    private void startOrResume() {
        pause.setImageResource(R.mipmap.ic_media_pause);
        if (prepared) {
            if (mOnMediaPlayListener != null) {
                mOnMediaPlayListener.onPlayResume(sources.get(currIndex));
            }
            //resume
            mediaPlayer.start();
        } else {
            if (mOnMediaPlayListener != null) {
                mOnMediaPlayListener.onPlayStart(sources.get(currIndex));
            }
            //start
            resetAndPlay(currIndex);
        }
    }

    private void playLast() {
        currIndex--;
        if (currIndex < 0) {
            if (mOnMediaPlayListener != null) {
                mOnMediaPlayListener.onPlayError(NO_LAST);
            }
            currIndex = 0;
            return;
        }
        resetAndPlay(currIndex);
    }

    private void playNext() {
        currIndex++;
        if (currIndex == sources.size()) {
            if (mOnMediaPlayListener != null) {
                mOnMediaPlayListener.onPlayError(NO_NEXT);
            }
            currIndex = sources.size() - 1;
            return;
        }
        resetAndPlay(currIndex);
    }

    private void resetAndPlay(int index) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        startPlay(index);
    }

    private void startPlay(int index) {
        pause.setImageResource(R.mipmap.ic_media_pause);
        DataSource dataSource = sources.get(index);
        if (mOnMediaPlayListener != null) {
            mOnMediaPlayListener.onPlayStart(dataSource);
        }
        if (dataSource != null) {
            try {
                prepared = true;
                mediaPlayer.setDataSource(getContext(), Uri.parse(dataSource.url));
                if (mOnMediaPlayPrepareListener != null) {
                    mOnMediaPlayPrepareListener.onPrepareStart();
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> void setDataSources(List<T> dataSources, DataTransform<T> dataTransform) {
        sources.clear();
        for (T dataSource : dataSources) {
            sources.add(dataTransform.transform(dataSource));
        }
    }

    public void release() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        timer.cancel();
        mediaPlayer.release();
    }

    public void setOnMediaPlayListener(OnMediaPlayListener value) {
        this.mOnMediaPlayListener = value;
    }

    public void setOnPlayerExtListenerExtListener(OnPlayerExtListenerExtListener value) {
        this.mOnPlayerExtListenerExtListener = value;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mOnMediaPlayPrepareListener != null) {
            mOnMediaPlayPrepareListener.onPrepareFinish();
        }
        mediaPlayer.start();
        duration = mediaPlayer.getDuration();
        seek.setMax(duration);
        if (seekToProgress == 0) {
            seek.setProgress(0);
        }
        totalTime.setText(MediaPlayerHelper.formatTime(duration));

        if (seekToProgress != 0) {
            mediaPlayer.seekTo(seekToProgress);
            seek.setProgress(seekToProgress);
            seekToProgress = 0;
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        prepared = false;
        pause.setImageResource(R.mipmap.ic_media_resume);
        playNext();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekToProgress = progress;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(progress);
        } else {
            resetAndPlay(currIndex);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setOnMediaPlayPrepareListener(OnMediaPlayPrepareListener value) {
        this.mOnMediaPlayPrepareListener = value;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        L.d("error,what:"+what+" ,extra"+extra);
        return false;
    }

    @IntDef({NO_LAST, NO_NEXT})
    public @interface ErrorStatus {

    }


    public interface OnPlayerExtListenerExtListener {
        void onMore();

        void onCollect();
    }

    public interface OnMediaPlayListener {
        void onPlayProgress(DataSource dataSource, int progress, int max);

        void onPlayStart(DataSource dataSource);

        void onPlayPause(DataSource dataSource);

        void onPlayResume(DataSource dataSource);

        void onPlayError(@ErrorStatus int errorStatus);
    }

    public interface OnMediaPlayPrepareListener {
        void onPrepareStart();

        void onPrepareFinish();
    }

    public interface DataTransform<T> {
        DataSource transform(T t);
    }

    public static class DataSource {
        private String url;

        public DataSource(String url) {
            this.url = url;
        }
    }
}
