package com.bian.image.viewer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bian.image.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * author 边凌
 * date 2017/7/11 18:15
 * 类描述：
 */

class ImageViewerAdapter extends PagerAdapter{
    private final List<String> datas;
    private final List<View> views;
    private final LayoutInflater inflater;
    ImageViewerAdapter(Context context, List<String> datas) {
        this.datas = datas;
        inflater=LayoutInflater.from(context);
        views=new ArrayList<>();
    }

    @Override
    public int getCount() {
        return datas!=null?datas.size():0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (position<views.size()){
            View view = views.get(position);
            container.removeView(view);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View inflate = inflater.inflate(R.layout.item_imageviewer, container, false);
        showPageData(position, inflate);
        views.add(inflate);
        container.addView(inflate);
        return inflate;
    }

    private void showPageData(int position, View inflate) {
        PhotoView pv= inflate.findViewById(R.id.imageViewerItem_pv);
        String s = datas.get(position);
        Glide.with(pv.getContext()).load(s).diskCacheStrategy(DiskCacheStrategy.ALL).into(pv);
    }
}
