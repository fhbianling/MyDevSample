package com.bian.mydevsample.ui.adaptertest;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.bian.base.baseclass.baseadapter.PullToRefresh;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

/**
 * author 边凌
 * date 2017/12/4 11:05
 * 类描述：
 */

public class AdapterTest extends BaseActivity {
    @Override
    protected int bindLayoutId() {
        return R.layout.fragment_1;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ListView mList = (ListView) findViewById(R.id.list);
        BookListAdapter bookListAdapter = new BookListAdapter(this, true);
        mList.setAdapter(bookListAdapter);
        bookListAdapter.bindToPullToRefreshLayout((PullToRefresh) findViewById(R.id.ptr),
                                                  PullToRefresh.Mode.Both);

        RecyclerView mRv = (RecyclerView) findViewById(R.id.list2);
        BookRecyclerViewAdapter bookRecyclerViewAdapter = new BookRecyclerViewAdapter(
                this);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(bookRecyclerViewAdapter);
        bookRecyclerViewAdapter.initLoad();
        bookRecyclerViewAdapter.bindToPullToRefreshLayout(
                (PullToRefresh) findViewById(R.id.ptr2),
                PullToRefresh.Mode.Both);
    }
}
