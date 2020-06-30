package com.bian.image.selector.internal.adapter;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bian.image.R;
import com.bian.image.selector.internal.ImgSelConfig;
import com.bian.image.selector.internal.bean.Folder;
import com.bian.image.selector.internal.common.OnFolderChangeListener;

import java.util.List;

public class FolderListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Folder> folderList;
    private final ImgSelConfig config;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList, ImgSelConfig config) {
        this.context = context;
        this.folderList = folderList;
        this.config = config;
    }

    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null && folders.size() > 0) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public int getSelectIndex() {
        return selected;
    }

    private void setSelectIndex(int position) {
        if (selected == position)
            return;
        if (listener != null)
            listener.onChange(position, folderList.get(position));
        selected = position;
        notifyDataSetChanged();
    }

    public void setOnFolderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return folderList != null ? folderList.size() : 0;
    }

    @Override
    public Folder getItem(int position) {
        return folderList != null && folderList.size() > position ? folderList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Folder folder = getItem(position);
        Holder holder;
        if (convertView == null) {
            holder = new Holder(context, parent);
            convertView = holder.getConvertView();
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position == 0) {
            holder.setText(R.id.tvFolderName, "所有图片");
            holder.setText(R.id.tvImageNum, "共" + getTotalImageSize() + "张");
            ImageView ivFolder = (ImageView) holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, ivFolder);
            }
        } else {
            holder.setText(R.id.tvFolderName, folder.name);
            holder.setText(R.id.tvImageNum, "共" + folder.images.size() + "张");
            ImageView ivFolder = (ImageView) holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, ivFolder);
            }
        }

        if (selected == position) {
            holder.setVisible(R.id.indicator, true);
        } else {
            holder.setVisible(R.id.indicator, false);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectIndex(position);
            }
        });
        return convertView;
    }

    private static class Holder {

        private final View convertView;

        Holder(Context context, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_img_sel_folder,
                                                               parent,
                                                               false);
        }

        View getConvertView() {
            return convertView;
        }

        void setVisible(@IdRes int idRes, boolean visible) {
            View view = getView(idRes);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        @NonNull
        View getView(@IdRes int idRes) {
            View viewById = convertView.findViewById(idRes);
            if (viewById == null) {
                throw new NullPointerException("can't find text view of " + idRes + "in this item view");
            }
            return viewById;
        }

        void setText(@IdRes int idRes, String text) {
            TextView view = (TextView) getView(idRes);
            view.setText(text);
        }
    }
}
