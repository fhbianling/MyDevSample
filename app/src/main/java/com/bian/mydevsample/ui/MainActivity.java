package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.bian.base.BaseUtilManager;
import com.bian.base.baseclass.baseadapter.PullToRefresh;
import com.bian.base.component.net.Api;
import com.bian.mydevsample.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseUtilManager.init(getApplication(), true);
        Api.setDebug(true);
        Api.setHttpLoggingEnable(true);
        Api.setBaseUrl("https://api.douban.com");

        ListView mList = (ListView) findViewById(R.id.list);
        MAdapter mAdapter = new MAdapter(this, true);
        mList.setAdapter(mAdapter);
        mAdapter.bindToPullToRefreshLayout((PullToRefresh) findViewById(R.id.ptr), PullToRefresh.Mode.Both);

        RecyclerView mRv = (RecyclerView) findViewById(R.id.list2);
        MAdapter2 mAdapter2 = new MAdapter2(this);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(mAdapter2);
        mAdapter2.initLoad();
        mAdapter2.bindToPullToRefreshLayout((PullToRefresh) findViewById(R.id.ptr2), PullToRefresh.Mode.Both);
    }
}
