package com.bian.mydevsample.ui.fragment.button;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bian.base.baseclass.AbsBaseFragment;
import com.bian.mydevsample.R;
import com.bian.mydevsample.ui.WeiTuanAnimTestActivity;
import com.bian.mydevsample.ui.daggerstudy.Dagger2StudyActivity;
import com.bian.mydevsample.ui.media.MediaPlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/10/13 15:19
 * 类描述：
 */

public class Fragment6 extends AbsBaseFragment implements AdapterView.OnItemClickListener {
    private ListView buttonList;
    private Adapter adapter;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_6, container, false);
    }

    @Override
    protected void initView(View rootView) {
        buttonList = (ListView) rootView.findViewById(R.id.buttonList);
        List<ButtonDesc> buttonDescs = new ArrayList<>();
        buttonDescs.add(new ButtonDesc("音乐播放器", 0));
        buttonDescs.add(new ButtonDesc("微团项目动画测试", 1));
        buttonDescs.add(new ButtonDesc("Dagger2学习", 2));
        adapter = new Adapter(buttonDescs, getActivity());
        buttonList.setAdapter(adapter);
        buttonList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ButtonDesc item = adapter.getItem(position);
        if (item != null) {
            onClick(item.id);
        }
    }

    private void onClick(int id) {
        switch (id) {
            case 0:
                MediaPlayerActivity.start(getContext(), "");
                break;
            case 1:
                WeiTuanAnimTestActivity.start(getContext());
                break;
            case 2:
                Dagger2StudyActivity.start(getContext());
                break;
        }
    }
}
