package com.bian.mydevsample.ui._main;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bian.base.baseclass.AbsBaseActivity;
import com.bian.mydevsample.R;
import com.bian.mydevsample.ui.adaptertest.AdapterTest;
import com.bian.mydevsample.ui.daggerstudy.Dagger2StudyActivity;
import com.bian.mydevsample.ui.infotextview.InfoTextViewSampleActivity;
import com.bian.mydevsample.ui.media.MediaPlayerActivity;
import com.bian.mydevsample.ui.pageanim1.PagerAnimTest1;
import com.bian.mydevsample.ui.pageanim2.PagerAnimTest2;
import com.bian.mydevsample.ui.pageanim3qidian.QiDianViewPagerAnimActivity;
import com.bian.mydevsample.ui.pageanim4.PageAnimFadeActivity;
import com.bian.mydevsample.ui.qqclean.QQCleanAnimActivity;
import com.bian.mydevsample.ui.weituananim1.AnimViewExecutorTest;
import com.bian.mydevsample.ui.weituananim2.FloatingAnimTest;
import com.bian.mydevsample.ui.weituananim3.WeiTuanAnimTestActivity;
import com.bian.mydevsample.ui.zhihuad.ZhiHuAdActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbsBaseActivity
        implements AdapterView.OnItemClickListener {
    private Fragment[] fragments = new Fragment[6];
    private Adapter adapter;

    @Override
    protected void beforeOnCreate() {
        super.beforeOnCreate();
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                if ("TextView".equals(name)) {
                    TextView textView = new TextView(context, attrs);
                    textView.setTypeface(Typeface.create("", Typeface.ITALIC));
                    return textView;
                }
                return null;
            }

            @Override
            public View onCreateView(View parent, String name, Context context,
                                     AttributeSet attrs) {
                if ("TextView".equals(name)) {
                    TextView textView = new TextView(context, attrs);
                    textView.setTypeface(Typeface.create("", Typeface.ITALIC));
                    return textView;
                }
                return null;
            }
        });
    }

    @Override
    protected int bindLayoutId() {
        return R.layout.activity_main_button;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ListView buttonList = this.findViewById(R.id.buttonList);
        List<ButtonDesc> buttonDescs = new ArrayList<>();
        buttonDescs.add(new ButtonDesc("音乐播放器", MediaPlayerActivity.class));
        buttonDescs.add(new ButtonDesc("adapter封装测试", AdapterTest.class));
        buttonDescs.add(new ButtonDesc("微团动画1", AnimViewExecutorTest.class));
        buttonDescs.add(new ButtonDesc("微团动画2", FloatingAnimTest.class));
        buttonDescs.add(new ButtonDesc("微团动画3", WeiTuanAnimTestActivity.class));
        buttonDescs.add(new ButtonDesc("Dagger2学习", Dagger2StudyActivity.class));
        buttonDescs.add(new ButtonDesc("InfoTextView封装sample", InfoTextViewSampleActivity.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画1", PagerAnimTest1.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画2", PagerAnimTest2.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画3仿起点", QiDianViewPagerAnimActivity.class));
        buttonDescs.add(new ButtonDesc("知乎广告效果模仿", ZhiHuAdActivity.class));
        buttonDescs.add(new ButtonDesc("QQ清理空间动画效果", QQCleanAnimActivity.class));
        buttonDescs.add(new ButtonDesc("相册淡入淡出", PageAnimFadeActivity.class));
        adapter = new Adapter(buttonDescs, this);
        buttonList.setAdapter(adapter);
        buttonList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ButtonDesc item = adapter.getItem(position);
        if (item != null) {
            startActivity(item.target);
        }
    }
}
