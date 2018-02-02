package com.bian.mydevsample.ui.weituananim2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bian.util.core.ToastUtil;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/12/4 11:08
 * 类描述：
 */

public class FloatingAnimTest extends BaseActivity implements View.OnClickListener {
    private int count;
    private TextView tv;
    private FloatingAnimSubmitter submiter;
    private TextView pause;

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_floatinganimtest;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toastOneTall).setOnClickListener(this);
        findViewById(R.id.toastOneShort).setOnClickListener(this);
        findViewById(R.id.toastADialog).setOnClickListener(this);
        pause = (TextView) findViewById(R.id.pause);
        pause.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.toastDelay);
        submiter = new FloatingAnimSubmitter(this);
        submiter.setInitHeight(50);
    }

    @Override
    public void onClick(View v) {
        count++;
        switch (v.getId()) {
            case R.id.toastOneShort:
                tv.setText("点击次数：" + count);
                submiter.execute(getToastView(false));
                break;
            case R.id.toastOneTall:
                tv.setText("点击次数：" + count);
                submiter.execute(getToastView(true));
                break;
            case R.id.pause:
                if (submiter.isPause()) {
                    pause.setText("暂停");
                    submiter.setResume();
                } else {
                    pause.setText("开始");
                    submiter.setPause();
                }
                break;
            case R.id.toastADialog:
                final View view = LayoutInflater.from(this).inflate(R.layout.dialog_test,
                                                                    (ViewGroup) this
                                                                            .getWindow()
                                                                            .getDecorView(),
                                                                    false);
                view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submiter.cancel(view);
                    }
                });
                submiter.execute(view);
                break;
        }
    }

    private View getToastView(boolean isTall) {
        final TextView view = new TextView(this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToastShort(view.getText().toString());
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(isTall ? 200 : 100,
                                                                         isTall ? 200 : 100);
        view.setLayoutParams(layoutParams);
        view.setTextSize(32);
        view.setText(String.valueOf(count));
        view.setPadding(15, 10, 15, 10);
        view.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.BLACK);
        return view;
    }
}
