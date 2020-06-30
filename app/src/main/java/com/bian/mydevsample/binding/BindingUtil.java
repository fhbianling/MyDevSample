package com.bian.mydevsample.binding;

import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bian.image.loader.GlideUtil;

/**
 * author 边凌
 * date 2017/9/13 20:38
 * 类描述：
 */

public class BindingUtil {

    @BindingAdapter("app:img")
    public static void img(ImageView imageView, String url) {
        GlideUtil.load(imageView, url);
    }
}
