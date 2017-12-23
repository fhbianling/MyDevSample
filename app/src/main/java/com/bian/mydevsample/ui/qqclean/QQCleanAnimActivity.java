package com.bian.mydevsample.ui.qqclean;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/12/6 10:00
 * 类描述：
 */

public class QQCleanAnimActivity extends BaseActivity {

    private QQCleanAnimView radar;
    private TextView tv;

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_qqcleananim;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv = findViewById(R.id.tv);
        radar = findViewById(R.id.radar);
        mockRadarProcess();
    }

    private void mockRadarProcess() {
        if (radar.isRunning()) return;
        radar.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count <= 100) {
                    final int finalCount = count;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(String.format("%d%%", finalCount));
                        }
                    });
                    count++;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        radar.stop();
                    }
                });
            }
        }).start();
    }

    public void start(View view) {
        mockRadarProcess();
    }
}
