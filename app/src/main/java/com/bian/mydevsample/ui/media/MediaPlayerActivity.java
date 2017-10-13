package com.bian.mydevsample.ui.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bian.base.util.utilbase.ToastUtil;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/10/13 15:27
 * 类描述：
 */

public class MediaPlayerActivity extends BaseActivity
        implements MediaPlayerView.OnPlayerExtListenerExtListener,
                   MediaPlayerView.OnMediaPlayListener, MediaPlayerView.OnMediaPlayPrepareListener {

    private static final String KEY_URL = "url";
    private MediaPlayerView mediaPlayerView;
    private TextView tv;

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, MediaPlayerActivity.class);
        starter.putExtra(KEY_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_media;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView);
        tv = (TextView) findViewById(R.id.tv);
        List<String> urls = new ArrayList<>();
        urls.add("http://xmdx.sc.chinaz.com/Files/DownLoad/sound1/201702/8369.wav");
        urls.add("http://fjdx.sc.chinaz.com/files/download/sound1/201310/3653.mp3");
        urls.add("http://gddx.sc.chinaz.com/files/download/sound1/201202/655.wav");
        mediaPlayerView.setDataSources(urls, new MediaPlayerView.DataTransform<String>() {
            @Override
            public MediaPlayerView.DataSource transform(String s) {
                return new MediaPlayerView.DataSource(s);
            }
        });
        mediaPlayerView.setOnPlayerExtListenerExtListener(this);
        mediaPlayerView.setOnMediaPlayListener(this);
        mediaPlayerView.setOnMediaPlayPrepareListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerView.release();
    }

    @Override
    public void onMore() {
        ToastUtil.showToastShort("弹出更多弹窗");
    }

    @Override
    public void onCollect() {
        ToastUtil.showToastShort("点击了收藏按钮");
    }

    @Override
    public void onPlayProgress(MediaPlayerView.DataSource dataSource, int progress, int max) {

    }

    @Override
    public void onPlayStart(MediaPlayerView.DataSource dataSource) {
        ToastUtil.showToastShort("播放开始");
    }

    @Override
    public void onPlayPause(MediaPlayerView.DataSource dataSource) {
        ToastUtil.showToastShort("播放暂停");
    }

    @Override
    public void onPlayResume(MediaPlayerView.DataSource dataSource) {
        ToastUtil.showToastShort("播放恢复");
    }

    @Override
    public void onPlayError(@MediaPlayerView.ErrorStatus int errorStatus) {
        switch (errorStatus) {
            case MediaPlayerView.NO_LAST:
                ToastUtil.showToastShort("没有上一首了");
                break;
            case MediaPlayerView.NO_NEXT:
                ToastUtil.showToastShort("没有下一首了");
                break;
        }
    }

    @Override
    public void onPrepareStart() {
        tv.setText("准备资源中...");
    }

    @Override
    public void onPrepareFinish() {
        tv.setText("准备完成");

    }
}
