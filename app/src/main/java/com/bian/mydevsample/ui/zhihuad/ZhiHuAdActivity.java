package com.bian.mydevsample.ui.zhihuad;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.adapter.AbsRVAdapter;
import com.bian.adapter.RvDefaultHolder;
import com.bian.image.loader.GlideUtil;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

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
                item.img = "http://img1.gamersky.com/image2017/12/20171205_zq_281_2/gamersky_02origin_03_201712592056B.jpg";
            } else {
                item.type = Item.TYPE_ITEM;
                item.title = title;
                item.msg = info;
            }
            mockDatas.add(item);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new Adapter(mockDatas, this));
    }


    private class Item {
        private final static int TYPE_AD = 1;
        private final static int TYPE_ITEM = 2;
        private int type;
        private String img;
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
            switch (viewType) {
                case Item.TYPE_AD:
                    view = inflater.inflate(R.layout.item_zhihuad_ad, parent, false);
                    break;
                default:
                    view = inflater.inflate(R.layout.item_zhiduad_item, parent, false);
                    break;
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
                    GlideUtil.loadBitmap(getContext(), item.img, new GlideUtil.BitmapLoader() {
                        @Override
                        public void onResourceReady(Bitmap resource) {
                            holder.setImageRes(R.id.zhiHuAd, resource);
                        }
                    });
                    break;
                case Item.TYPE_ITEM:
                    holder.setText(R.id.title, item.title);
                    holder.setText(R.id.info, item.msg);
                    break;
            }
        }
    }
}
