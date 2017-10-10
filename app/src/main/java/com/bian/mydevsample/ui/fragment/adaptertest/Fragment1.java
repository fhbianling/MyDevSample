package com.bian.mydevsample.ui.fragment.adaptertest;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.base.baseclass.baseadapter.PullToRefresh;
import com.bian.mydevsample.R;

/**
 * author 边凌
 * date 2017/10/9 15:46
 * 类描述：
 */

public class Fragment1 extends AbsBaseFragment {
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    protected void initView(View rootView) {
        ListView mList = (ListView) rootView.findViewById(R.id.list);
        BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), true);
        mList.setAdapter(bookListAdapter);
        bookListAdapter.bindToPullToRefreshLayout((PullToRefresh) rootView.findViewById(R.id.ptr),
                PullToRefresh.Mode.Both);

        RecyclerView mRv = (RecyclerView) rootView.findViewById(R.id.list2);
        BookRecyclerViewAdapter bookRecyclerViewAdapter = new BookRecyclerViewAdapter(
                getActivity());
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(bookRecyclerViewAdapter);
        bookRecyclerViewAdapter.initLoad();
        bookRecyclerViewAdapter.bindToPullToRefreshLayout(
                (PullToRefresh) rootView.findViewById(R.id.ptr2),
                PullToRefresh.Mode.Both);
    }
}
