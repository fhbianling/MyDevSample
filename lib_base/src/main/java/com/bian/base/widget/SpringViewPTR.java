package com.bian.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.base.R;
import com.bian.base.baseclass.baseadapter.BaseRecycleViewPTRAdapter;
import com.bian.base.baseclass.baseadapter.PullToRefresh;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.container.BaseFooter;
import com.liaoinstan.springview.container.BaseHeader;
import com.liaoinstan.springview.widget.SpringView;


/**
 * author 边凌
 * date 2017/4/26 15:55
 * desc ${用于和{@link BaseRecycleViewPTRAdapter}相配合，
 * 使用该子类且实现{@link PullToRefresh}接口，达到上下拉刷新在adapter中直接控制}
 *
 * 若发现该类无法满足需求，可以继承其他上下拉刷新ViewGroup的第三方
 */

public class SpringViewPTR extends SpringView implements PullToRefresh {

    public SpringViewPTR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMode(Mode mode) {
        switch (mode) {
            case Both:
                setHeader(new AliHeader(getContext()));
                setFooter(new AliFooter(getContext()));
                break;
            case PullFromEnd:
                setHeader(new NullViewHeader());
                setFooter(new AliFooter(getContext()));
                break;
            case PullFromStart:
                setHeader(new AliHeader(getContext()));
                setFooter(new NullViewFooter());
                break;
        }
    }

    @Override
    public void onRefreshComplete() {
        onFinishFreshAndLoad();
    }

    @Override
    public void setOnRefreshListener(final OnRefreshListener onRefreshListener) {
        setListener(new OnFreshListener() {
            @Override
            public void onRefresh() {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefreshDown();
                }
            }

            @Override
            public void onLoadmore() {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefreshUp();
                }
            }
        });
    }

    private class NullViewHeader extends BaseHeader{

        @Override
        public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
            return inflater.inflate(R.layout.ptr_nullheader,viewGroup,false);
        }

        @Override
        public void onPreDrag(View rootView) {

        }

        @Override
        public void onDropAnim(View rootView, int dy) {

        }

        @Override
        public void onLimitDes(View rootView, boolean upORdown) {

        }

        @Override
        public void onStartAnim() {

        }

        @Override
        public void onFinishAnim() {

        }
    }

    private class NullViewFooter extends BaseFooter{

        @Override
        public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
            return inflater.inflate(R.layout.ptr_nullheader,viewGroup,false);
        }

        @Override
        public void onPreDrag(View rootView) {

        }

        @Override
        public void onDropAnim(View rootView, int dy) {

        }

        @Override
        public void onLimitDes(View rootView, boolean upORdown) {

        }

        @Override
        public void onStartAnim() {

        }

        @Override
        public void onFinishAnim() {

        }
    }

}
