package com.bian.mydevsample.ui._main;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bian.base.AbsBaseActivity;
import com.bian.mydevsample.R;
import com.bian.mydevsample.ui.adapterbasetest.BaseTest;
import com.bian.mydevsample.ui.backstage.BackStageActivity;
import com.bian.mydevsample.ui.customtab.CustomTabActivity;
import com.bian.mydevsample.ui.flipover.FlipOverActivity;
import com.bian.mydevsample.ui.flower.FlowerActivity;
import com.bian.mydevsample.ui.indicatorbar.TemplateIndicatorTest;
import com.bian.mydevsample.ui.infotextview.InfoTextViewSampleActivity;
import com.bian.mydevsample.ui.kugouanim.KuGouMainAnimActivity;
import com.bian.mydevsample.ui.media.MediaPlayerActivity;
import com.bian.mydevsample.ui.pageanim1.PagerAnimTest1;
import com.bian.mydevsample.ui.pageanim2.PagerAnimTest2;
import com.bian.mydevsample.ui.pageanim3qidian.QiDianViewPagerAnimActivity;
import com.bian.mydevsample.ui.pageanim4.PageAnimFadeActivity;
import com.bian.mydevsample.ui.qqclean.QQCleanAnimActivity;
import com.bian.mydevsample.ui.randombubbles.RandomBubblesDemo;
import com.bian.mydevsample.ui.randomcards.RandomCardActivity;
import com.bian.mydevsample.ui.rotatebox.RotateBox;
import com.bian.mydevsample.ui.rotatebox.RotateBoxDemo;
import com.bian.mydevsample.ui.stream.MusicStreamActivity;
import com.bian.mydevsample.ui.wheeelview2.WheelViewDemo2;
import com.bian.mydevsample.ui.wheelview.WheelViewDemo;
import com.bian.mydevsample.ui.zhihuad.ZhiHuAdActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.LayoutInflaterCompat;

public class MainActivity extends AbsBaseActivity
        implements AdapterView.OnItemClickListener {
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
//                    changeBg(textView);
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
//                    changeBg(textView);
                    return textView;
                }
                return null;
            }

//            private void changeBg(TextView textView) {
//                OvalShape ovalShape = new OvalShape() {
//                    @Override
//                    public void draw(Canvas canvas, Paint paint) {
//                        int orgColor = paint.getColor();
//                        paint.setColor(Color.RED);
//                        canvas.drawOval(rect(), paint);
//                        paint.setColor(orgColor);
//                    }
//                };
//                ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
//                textView.setBackground(shapeDrawable);
//            }
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
        buttonDescs.add(new ButtonDesc("adapter基础测试", BaseTest.class));
//        buttonDescs.add(new ButtonDesc("adapter封装测试", AdapterTest.class));
        buttonDescs.add(new ButtonDesc("InfoTextView封装sample", InfoTextViewSampleActivity.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画1", PagerAnimTest1.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画2", PagerAnimTest2.class));
        buttonDescs.add(new ButtonDesc("ViewPager动画3仿起点", QiDianViewPagerAnimActivity.class));
        buttonDescs.add(new ButtonDesc("知乎广告效果模仿", ZhiHuAdActivity.class));
        buttonDescs.add(new ButtonDesc("QQ清理空间动画效果", QQCleanAnimActivity.class));
        buttonDescs.add(new ButtonDesc("相册淡入淡出", PageAnimFadeActivity.class));
        buttonDescs.add(new ButtonDesc("后台Toast测试", BackStageActivity.class));
        buttonDescs.add(new ButtonDesc("酷狗翻页动画", KuGouMainAnimActivity.class));
        buttonDescs.add(new ButtonDesc("翻页动画", FlipOverActivity.class));
        buttonDescs.add(new ButtonDesc("雪花动画", FlowerActivity.class));
        buttonDescs.add(new ButtonDesc("波形测试", MusicStreamActivity.class));
        buttonDescs.add(new ButtonDesc("刻度条测试", TemplateIndicatorTest.class));
        buttonDescs.add(new ButtonDesc("自定义TabLayout", CustomTabActivity.class));
        buttonDescs.add(new ButtonDesc("随机大小布局", RandomCardActivity.class));
        buttonDescs.add(new ButtonDesc("随机气泡", RandomBubblesDemo.class));
        buttonDescs.add(new ButtonDesc("自定义滚轮", WheelViewDemo.class));
        buttonDescs.add(new ButtonDesc("自定义滚轮2-graphic.matrix实现", WheelViewDemo2.class));
        buttonDescs.add(new ButtonDesc("旋转盒子", RotateBoxDemo.class));
//        buttonDescs.add(new ButtonDesc("Bitmap合成", BitmapComposite.class));
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
