package com.bian.image.loader;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bian.image.viewer.ImageViewer;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

// TODO: 2017/8/25
public class GlideUtil {

    private static Drawable sErrorDrawable = new ColorDrawable(Color.parseColor("#cccccc"));

    /**
     * @param url A file path, or a uri or url
     */
    public static void load(ImageView imageView, String url) {
        load(imageView, url, null);
    }

    /**
     * @param url A file path, or a uri or url
     */
    public static void load(ImageView imageView, String url, @DrawableRes int errorDrawableRes) {
        load(imageView, url, imageView.getContext().getResources().getDrawable(errorDrawableRes));
    }

    /**
     * @param url A file path, or a uri or url
     */
    @SuppressWarnings("WeakerAccess")
    public static void load(final ImageView imageView, final String url, @Nullable Drawable errorDrawable) {
        if (errorDrawable == null) {
            errorDrawable=sErrorDrawable;
        }
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .placeholder(errorDrawable)
                .error(errorDrawable).into(imageView);

        if (!imageView.isClickable()) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewer.start(imageView.getContext(), url);
                }
            });
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void load(ImageView imageView, @DrawableRes int drawableRes) {
        Glide.with(imageView.getContext()).
                load(drawableRes).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                crossFade().
                into(imageView);
    }

    /**
     * 圆形
     *
     * @param url A file path, or a uri or url
     */
    @SuppressWarnings("unused")
    public static void loadCircle(ImageView imageView, String url) {
        loadCircle(imageView, url, null);

    }

    /**
     * 圆形
     *
     * @param url A file path, or a uri or url
     */
    public static void loadCircle(ImageView imageView, String url, @DrawableRes int errorResId) {
        loadCircle(imageView, url, imageView.getContext().getResources().getDrawable(errorResId));
    }

    /**
     * 圆形
     *
     * @param url A file path, or a uri or url
     */
    @SuppressWarnings("WeakerAccess")
    public static void loadCircle(ImageView imageView, String url, Drawable errorDrawable) {

        DrawableRequestBuilder<String> stringDrawableRequestBuilder = Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .crossFade();
        if (errorDrawable != null) {
            stringDrawableRequestBuilder
                    .placeholder(errorDrawable)
                    .error(errorDrawable);
        }
        stringDrawableRequestBuilder.into(imageView);
    }

    /**
     * 圆形
     *
     * @param url A file path, or a uri or url
     */
    @SuppressWarnings("WeakerAccess")
    public static void loadCircle(ImageView imageView, int url, Drawable errorDrawable) {
        DrawableRequestBuilder<Integer> integerDrawableRequestBuilder = Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(imageView.getContext()))
                .crossFade();
        if (errorDrawable != null) {
            integerDrawableRequestBuilder
                    .placeholder(errorDrawable)
                    .error(errorDrawable);
        }
        integerDrawableRequestBuilder.into(imageView);
    }

    public static void loadCircle(ImageView imageView, int url) {
        loadCircle(imageView, url, null);
    }

    public static void loadRound(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).
                load(url).
                transform(new GlideRoundTransform(imageView.getContext(), 5)).
                into(imageView);
    }

    public static void loadAsBitmap(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).
                load(url).
                asBitmap().
                into(imageView);
    }

    public static void loadAsBitmap(ImageView imageView, String url, int errorRes) {
        Glide.with(imageView.getContext()).
                load(url).
                asBitmap().
                error(errorRes).
                into(imageView);
    }

    public interface OnDiskClearListener {
        void clearStart();

        void clearFinish();
    }

}
