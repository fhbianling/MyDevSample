package com.bian.mydevsample.ui.adaptertest;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ListView;
import android.widget.TextView;

import com.bian.adapter.IPtr;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;
import com.bian.mydevsample.bean.BookBean;

import java.util.List;

/**
 * author 边凌
 * date 2017/12/4 11:05
 * 类描述：
 */

public class AdapterTest extends BaseActivity {

    private TextView listInfo;
    private TextView rvInfo;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private BookListAdapter bookListAdapter;

    @Override
    protected int bindLayoutId() {
        return R.layout.fragment_1;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        listInfo = findViewById(R.id.info1);
        rvInfo = findViewById(R.id.info2);

        ListView mList = findViewById(R.id.list);
        RecyclerView mRv = findViewById(R.id.list2);

        bookListAdapter = new BookListAdapter(this);
        mList.setAdapter(bookListAdapter);
        bookListAdapter.bindPtrLayout((IPtr.PullToRefresh) findViewById(R.id.ptr),
                                      IPtr.PtrMode.Both);
        bookRecyclerViewAdapter = new BookRecyclerViewAdapter(
                this);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(bookRecyclerViewAdapter);
        bookRecyclerViewAdapter.bindPtrLayout(
                (IPtr.PullToRefresh) findViewById(R.id.ptr2),
                IPtr.PtrMode.Both);

        bookRecyclerViewAdapter.firstLoad();
        bookListAdapter.firstLoad();
        initAdptListener();
    }

    private void initAdptListener() {
        bookRecyclerViewAdapter.setOnDataLoadListener(new IPtr.OnDataLoadListener() {

            @Override public void onLoadSuccess(IPtr.LoadType loadType, int pageNum) {
                CharSequence type = rvInfo.getText();
                List<BookBean> data = bookRecyclerViewAdapter.getData();
                String info = type + "\n页数:" + bookRecyclerViewAdapter.getPageNum() +
                        "\n数据总数:" + (data != null ? data.size() : -1);
                rvInfo.setText(info);
            }

            @Override
            public void onLoadFailed(IPtr.LoadType loadType, int errorCode, @Nullable String msg) {

            }

            @Override
            public void onLoadStart(IPtr.LoadType type) {
                rvInfo.setText(type.toString());
            }
        });
        bookListAdapter.setOnDataLoadListener(new IPtr.OnDataLoadListener() {

            @Override public void onLoadSuccess(IPtr.LoadType loadType, int pageNum) {
                CharSequence type = listInfo.getText();
                List<BookBean> data = bookListAdapter.getData();
                String info = type + "\n页数:" + bookListAdapter.getPageNum() +
                        "\n数据总数:" + (data != null ? data.size() : -1);
                listInfo.setText(info);
            }

            @Override
            public void onLoadFailed(IPtr.LoadType loadType, int errorCode, @Nullable String msg) {

            }

            @Override
            public void onLoadStart(IPtr.LoadType type) {
                listInfo.setText(type.toString());
            }
        });
    }
}
