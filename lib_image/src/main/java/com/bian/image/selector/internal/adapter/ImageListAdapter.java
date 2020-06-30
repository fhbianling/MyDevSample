package com.bian.image.selector.internal.adapter;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bian.image.R;
import com.bian.image.selector.internal.ImgSelConfig;
import com.bian.image.selector.internal.bean.Image;
import com.bian.image.selector.internal.common.Constant;
import com.bian.image.selector.internal.common.OnItemClickListener;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.Holder> {

    private static final int TYPE_SHOW_CAMERA = 1;
    private static final int TYPE_NORMAL = 0;
    private boolean showCamera;
    private boolean multiSelect;

    private final List<Image> list;
    private final ImgSelConfig config;
    private final Context context;
    private OnItemClickListener listener;
    private final LayoutInflater inflater;

    public ImageListAdapter(Context context, List<Image> list, ImgSelConfig config) {
        this.context = context;
        this.list = list;
        this.config = config;
        inflater = LayoutInflater.from(context);
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_SHOW_CAMERA) {
            itemView = inflater.inflate(R.layout.item_img_sel_take_photo, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.item_img_sel, parent, false);
        }
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int pos) {

        if (list == null) {
            return;
        }
        final Image item = list.get(holder.getAdapterPosition());

        if (holder.getAdapterPosition() == 0 && showCamera) {
            ImageView iv = (ImageView) holder.getView(R.id.ivTakePhoto);
            iv.setImageResource(R.drawable.ic_take_photo);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(holder.getAdapterPosition(), item);
                }
            });
            return;
        }

        if (multiSelect) {
            holder.getView(R.id.ivPhotoCheaked).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(holder.getAdapterPosition(), item);
                        if (ret == 1) { // 局部刷新
                            if (Constant.imageList.contains(item.path)) {
                                holder.setImageResource(R.id.ivPhotoCheaked,
                                                        R.drawable.ic_checked);
                            } else {
                                holder.setImageResource(R.id.ivPhotoCheaked,
                                                        R.drawable.ic_uncheck);
                            }
                        }
                    }
                }
            });
        }

        holder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onImageClick(holder.getAdapterPosition(), item);
            }
        });

        final ImageView iv = (ImageView) holder.getView(R.id.ivImage);
        config.loader.displayImage(context, item.path, iv);

        if (multiSelect) {
            holder.setVisible(R.id.ivPhotoCheaked, true);
            if (Constant.imageList.contains(item.path)) {
                holder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_checked);
            } else {
                holder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_uncheck);
            }
        } else {
            holder.setVisible(R.id.ivPhotoCheaked, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return TYPE_SHOW_CAMERA;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder {

        Holder(View itemView) {
            super(itemView);
        }

        View getView(@IdRes int idRes) {
            View viewById = itemView.findViewById(idRes);
            if (viewById == null) {
                throw new NullPointerException("can't find view of id:" + idRes);
            }
            return viewById;
        }

        void setVisible(@IdRes int idRes, boolean visible) {
            getView(idRes).setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        void setImageResource(@IdRes int idRes, @DrawableRes int imgRes) {
            ImageView imageView = (ImageView) getView(idRes);
            imageView.setImageResource(imgRes);
        }

        void setOnItemViewClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
