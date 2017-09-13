package com.bian.mydevsample;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.bian.base.baseclass.PullToRefresh;

/**
 * author 边凌
 * date 2017/9/13 20:45
 * 类描述：
 */

public class MSwipeRefreshLayout extends SwipeRefreshLayout implements
                                                            PullToRefresh {

    public MSwipeRefreshLayout(Context context) {
        super(context);
    }

    public MSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMode(Mode mode) {

    }

    @Override
    public void onRefreshComplete() {
        setRefreshing(false);
    }

    @Override
    public void setOnRefreshListener(
            final PullToRefresh.OnRefreshListener onRefreshListener) {
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "下拉刷新", Toast.LENGTH_SHORT).show();
                onRefreshListener.onRefreshDown();
            }
        });
        View childAt = getChildAt(0);
        if (childAt instanceof ListView) {
            ((ListView) childAt).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                     int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        onRefreshListener.onRefreshUp();
                        Toast.makeText(getContext(), "上拉加载更多", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
