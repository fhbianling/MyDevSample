package com.bian.image.selector.internal.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bian.image.R;
import com.bian.image.selector.internal.ImgSelConfig;
import com.bian.image.selector.internal.bean.Image;
import com.bian.image.selector.internal.common.Constant;
import com.bian.image.selector.internal.common.OnItemClickListener;

import java.util.List;

public class PreviewAdapter extends PagerAdapter {

    private final Activity activity;
    private final List<Image> images;
    private final ImgSelConfig config;
    private OnItemClickListener listener;

    public PreviewAdapter(Activity activity, List<Image> images, ImgSelConfig config) {
        this.activity = activity;
        this.images = images;
        this.config = config;
    }

    @Override
    public int getCount() {
        if (config.needCamera)
            return images.size() - 1;
        else
            return images.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, final int position) {
        View root = View.inflate(activity, R.layout.item_pager_img_sel, null);
        final ImageView photoView = root.findViewById(R.id.ivImage);
        final ImageView ivChecked = root.findViewById(R.id.ivPhotoCheaked);

        if (config.multiSelect) {

            ivChecked.setVisibility(View.VISIBLE);
            final Image image = images.get(config.needCamera ? position + 1 : position);
            if (Constant.imageList.contains(image.path)) {
                ivChecked.setImageResource(R.drawable.ic_checked);
            } else {
                ivChecked.setImageResource(R.drawable.ic_uncheck);
            }

            ivChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, image);
                        if (ret == 1) { // 局部刷新
                            if (Constant.imageList.contains(image.path)) {
                                ivChecked.setImageResource(R.drawable.ic_checked);
                            } else {
                                ivChecked.setImageResource(R.drawable.ic_uncheck);
                            }
                        }
                    }
                }
            });

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageClick(position, images.get(position));
                    }
                }
            });
        } else {
            ivChecked.setVisibility(View.GONE);
        }

        container.addView(root, ViewGroup.LayoutParams.MATCH_PARENT,
                          ViewGroup.LayoutParams.MATCH_PARENT);

        displayImage(photoView, images.get(config.needCamera ? position + 1 : position).path);

        return root;
    }

    private void displayImage(ImageView photoView, String path) {
        config.loader.displayImage(activity, path, photoView);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
