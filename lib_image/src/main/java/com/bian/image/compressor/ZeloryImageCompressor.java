package com.bian.image.compressor;

import android.content.Context;

import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author 边凌
 * date 2017/6/12 11:39
 * desc ${TODO}
 */

class ZeloryImageCompressor implements ImageCompressor {
    @Override
    public void compressImage(Context context, String filePath, final ImageOnCompressListener onCompressListener) {
        if (onCompressListener == null) {
            return;
        }
        new Compressor(context)
                .compressToFileAsFlowable(new File(filePath))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        onCompressListener.onStart();
                    }
                })
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        onCompressListener.onSuccess(file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        onCompressListener.onError(throwable);
                    }
                });
    }

    @Override
    public void compressImages(final Context context, List<String> filePaths, final ImageOnCompressListener onCompressListener) {
        final List<String> results = new ArrayList<>();
        Flowable.fromIterable(filePaths)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, Flowable<File>>() {
                    @Override
                    public Flowable<File> apply(@NonNull String s) throws Exception {
                        return new Compressor(context).compressToFileAsFlowable(new File(s));
                    }
                })
                .subscribe(new FlowableSubscriber<File>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        s.request(Long.MAX_VALUE);
                        onCompressListener.onStart();
                    }

                    @Override
                    public void onNext(File file) {
                        results.add(file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable t) {
                        onCompressListener.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        onCompressListener.onSuccess(results);
                    }
                });

    }
}
