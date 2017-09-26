package com.bian.mydevsample.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.bian.base.baseclass.baseadapter.PullToRefresh;
import com.bian.base.component.net.Api;
import com.bian.base.component.net.ApiCall;
import com.bian.base.component.net.ApiCallBack;
import com.bian.mydevsample.R;
import com.bian.mydevsample.bean.BookRequest;
import com.bian.mydevsample.net.BookService;
import com.bian.mydevsample.net.LogTestService;

import retrofit2.Call;

import static com.bian.mydevsample.consts.Const.BOOK_BASE_URL;
import static com.bian.mydevsample.consts.Const.TEST_LOG_URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//        Api.setBaseUrl(TEST_LOG_URL);
//        ApiCall apiCall = new ApiCall(this);
//        Call<Object> test = Api.getService(LogTestService.class).test("689");
//        apiCall.enqueue(test, new ApiCallBack<Object>() {
//            @Override
//            public void onSuccess(@NonNull Object o) {
//                Api.setBaseUrl(BOOK_BASE_URL);
//            }
//
//            @Override
//            public void onFailure(@Nullable Throwable throwable) {
//                Api.setBaseUrl(BOOK_BASE_URL);
//            }
//        });
    }
}
