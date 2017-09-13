package com.bian.mydevsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.bian.base.BaseUtilManager;
import com.bian.base.baseclass.AbsBaseAdapter;
import com.bian.base.baseclass.PullToRefresh;
import com.bian.base.component.net.Api;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseUtilManager.init(getApplication(), true);
        Api.setDebug(true);
        Api.setHttpLoggingEnable(true);
        Api.setBaseUrl("https://api.douban.com");

        ListView mList= (ListView) findViewById(R.id.list);
        MAdapter mAdapter=new MAdapter(this,true);
        mList.setAdapter(mAdapter);
        mAdapter.bindToPullToRefreshLayout((PullToRefresh) findViewById(R.id.ptr), AbsBaseAdapter.Mode.Both);
    }
}
