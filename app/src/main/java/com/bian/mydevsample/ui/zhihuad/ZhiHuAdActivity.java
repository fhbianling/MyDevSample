package com.bian.mydevsample.ui.zhihuad;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bian.adapter.AbsRVAdapter;
import com.bian.adapter.RvDefaultHolder;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author 边凌
 * date 2017/12/5 15:29
 * 类描述：
 */

public class ZhiHuAdActivity extends BaseActivity {
    @Override
    protected int bindLayoutId() {
        return R.layout.activity_zhihuad;
    }

    private Point size = new Point();
    @Override
    protected void initView(Bundle savedInstanceState) {
        RecyclerView rv = findViewById(R.id.rv);
        List<Item> mockDatas = new ArrayList<>();
        String title = "这里是模拟item标题";
        String info = "这里是模拟item内容";
        for (int i = 0; i < 3; i++) {
            info += info;
        }

        for (int i = 0; i < 13; i++) {
            Item item = new Item();
            if (i == 5) {
                item.type = Item.TYPE_AD;
//                item.img = R.mipmap.ic_test2;
            } else {
                item.type = Item.TYPE_ITEM;
                item.title = title;
                item.msg = info;
            }
            mockDatas.add(item);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        Adapter adapter = new Adapter(mockDatas, this);
        rv.setAdapter(adapter);
        AtomicBoolean first = new AtomicBoolean(true);
        rv.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (first.get()) {
                size.x = rv.getWidth();
                size.y = rv.getHeight();
                adapter.notifyDataSetChanged();
                first.set(false);
            }
        });
    }


    private static class Item {
        private final static int TYPE_AD = 1;
        private final static int TYPE_ITEM = 2;
        private int type;
        private @DrawableRes int img;
        private String title;
        private String msg;
    }

    private class Adapter extends AbsRVAdapter<Item, RvDefaultHolder> {

        Adapter(List<Item> data, Activity context) {
            super(data, context);
        }

        @Override
        protected RvDefaultHolder onCreateHolder(LayoutInflater inflater, ViewGroup parent,
                                                 int viewType) {
            View view;
            if (viewType == Item.TYPE_AD) {
                view = inflater.inflate(R.layout.item_zhihuad_ad, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_zhiduad_item, parent, false);
            }
            return new RvDefaultHolder(view, viewType);
        }

        @Override
        public int getItemViewType(int position) {
            Item item = getItem(position);
            if (item != null) {
                return item.type;
            }
            return super.getItemViewType(position);
        }

        @Override
        protected void bindView(int position, int viewType, @NonNull final RvDefaultHolder holder,
                                @NonNull Item item, boolean isLast) {
            switch (viewType) {
                case Item.TYPE_AD:
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), item.img);
                    ((ZhiHuAdImageView) holder.itemView).setSize(size.x, size.y);
                    holder.setImageRes(R.id.zhiHuAd, bitmap);
                    break;
                case Item.TYPE_ITEM:
                    holder.setText(R.id.title, item.title);
                    holder.setText(R.id.info, item.msg);
                    break;
            }
        }
    }
}
