package com.bian.mydevsample.ui.adapterbasetest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;
import com.bian.util.core.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2018/6/20 14:20
 * 类描述：
 */
public class BaseTest extends BaseActivity {
    private TestAdapter testAdapter;
    private InputDialog inputDialog;

    @Override protected int bindLayoutId() {
        return R.layout.activity_base_adapter_test;
    }

    @Override protected void initView(Bundle savedInstanceState) {
        RecyclerView view = findViewById(R.id.list);
        testAdapter = new TestAdapter(this);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(testAdapter);

        List<Integer> list = getRandomIntegers();
        testAdapter.resetData(list);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (inputDialog != null) {
            inputDialog.dismiss();
        }
    }

    public void reset(View view) {
        List<Integer> list = getRandomIntegers();
        ToastUtil.showToastShort("重置为随机数列集合");
        testAdapter.resetData(list);
    }

    public void addList(View view) {
        List<Integer> list = getRandomIntegers();
        ToastUtil.showToastShort("添加随机数列集合");
        testAdapter.addData(list);
    }

    public void addSingle(View view) {
        int data = (int) (Math.random() * 100 + 200);
        ToastUtil.showToastShort("添加单个数据:" + data);
        testAdapter.addData(data);
    }

    public void removeObj(View view) {
        if (inputDialog == null) {
            inputDialog = new InputDialog(this);
            inputDialog.setCallback(new InputDialog.Callback() {
                @Override public void onInput(long value) {
                    ToastUtil.showToastShort("移除指定数据:" + value);
                    testAdapter.removeData(Integer.decode(String.valueOf(value)));
                }
            });
        }
        inputDialog.show();
    }

    @NonNull private List<Integer> getRandomIntegers() {
        List<Integer> list = new ArrayList<>();
        int start = (int) (Math.random() * 100);
        int range = (int) (Math.random() * 30);
        for (int i = start; i < start + range; i++) {
            list.add(i);
        }
        return list;
    }
}
