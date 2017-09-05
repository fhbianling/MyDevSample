package com.bian.image.selector;

import android.content.Context;
import android.widget.ImageView;

import com.bian.image.selector.internal.ImageLoader;
import com.bumptech.glide.Glide;

/**
 * author 边凌
 * date 2017/7/2 18:43
 * desc ${TODO}
 */

public class GlideForSmuyyLoader implements ImageLoader {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
        Glide.with(context).load(path).into(imageView);
    }
}
