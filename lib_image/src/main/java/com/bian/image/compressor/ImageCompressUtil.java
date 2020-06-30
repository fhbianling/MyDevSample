package com.bian.image.compressor;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * author 边凌
 * date 2016/11/24 11:42
 * desc ${可配置dialog的图片压缩工具简单实现类}
 */

public class ImageCompressUtil implements ImageOnCompressListener {

    private Dialog dialog;
    private final CompressorImpl compressor;
    private final Context context;
    private OnMultiCompressListener multiCompressListener;
    private OnSingleCompressListener singleCompressListener;
    private OnFailedAction onFailedAction;

    private ImageCompressUtil(@NonNull Context context) {
        this.context = context.getApplicationContext();
        compressor = new CompressorImpl();
        compressor.setOnCompressListener(this);
    }

    public void setDialog(Dialog dialog){
        this.dialog=dialog;
    }
    public static ImageCompressUtil newInstance(Context context) {
        return new ImageCompressUtil(context);
    }

    public void release() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public ImageCompressUtil compress(@NonNull String file, @NonNull OnSingleCompressListener singleCompressListener) {
        this.singleCompressListener = singleCompressListener;
        compressor.compressImage(context, file, this);
        return this;
    }

    public ImageCompressUtil compress(@NonNull List<String> file, @NonNull OnMultiCompressListener multiCompressListener) {
        this.multiCompressListener = multiCompressListener;
        compressor.compressImages(context, file, this);
        return this;
    }

    public void onFailed(OnFailedAction onFailedAction) {
        this.onFailedAction = onFailedAction;
    }

    @Override
    public void onStart() {
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onSuccess(String file) {
        if (dialog != null) {
            dialog.dismiss();
        }

        if (singleCompressListener != null) {
            singleCompressListener.onCompressSuccess(file);
        }

    }

    @Override
    public void onSuccess(List<String> file) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (multiCompressListener != null) {
            multiCompressListener.onCompressSuccess(file);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (dialog != null) {
            dialog.dismiss();
        }

        if (onFailedAction != null) {
            onFailedAction.onFailed(throwable);
        }
    }


    public interface OnMultiCompressListener {
        void onCompressSuccess(List<String> files);
    }

    public interface OnSingleCompressListener {
        void onCompressSuccess(String file);
    }

    public interface OnFailedAction {
        void onFailed(Throwable throwable);
    }
}
