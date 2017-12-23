package com.bian.base.baseclass.baseadapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * author 边凌
 * date 2017/9/26 10:14
 * 类描述：
 */
public class RvDefaultHolder extends RecyclerView.ViewHolder {
    public static final int NONE = -1;
    private View root;
    private int type;
    private SparseArray<View> viewCache;

    public RvDefaultHolder(View root) {
        this(root, NONE);
    }

    public RvDefaultHolder(View root, int type) {
        super(root);
        this.root = root;
        this.type = type;
        viewCache = new SparseArray<>();
    }

    public int getType() {
        return type;
    }

    public
    @Nullable
    <T extends View> T get(@IdRes int id) {
        T t;
        View view = viewCache.get(id);
        try {
            if (view == null) {
                if (root.getId() == id) {
                    t = (T) root;
                } else {
                    t = (T) root.findViewById(id);
                }
                if (t != null) {
                    viewCache.put(id, t);
                }
            } else {
                t = (T) view;
            }
            return t;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final void setText(@IdRes int id, CharSequence text) {
        TextView textView = get(id);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public final void setImageRes(@IdRes int id, @DrawableRes int resId) {
        ImageView imageView = get(id);
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }

    public final void setImageRes(@IdRes int id, Drawable drawable) {
        ImageView imageView = get(id);
        if (imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public final void setImageRes(@IdRes int id, Bitmap bitmap) {
        ImageView imageView = get(id);
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
