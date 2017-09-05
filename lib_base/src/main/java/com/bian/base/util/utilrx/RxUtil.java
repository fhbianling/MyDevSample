package com.bian.base.util.utilrx;

import android.support.annotation.NonNull;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author 边凌
 * date 2017/6/14 15:59
 * 类描述：RxJava工具类
 */

@SuppressWarnings({"WeakerAccess"})
public class RxUtil {
    public static <T> FlowableTransformer<T, T> ioToMainFlowable() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    public static <T> ObservableTransformer<T, T> ioToMainObservable() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static Disposable createCountDownDisposable(int countDownSeconds,Consumer<Long> next, Consumer<Throwable> error, Action complete, Consumer<Subscription> onSubscripe){
        return Flowable.intervalRange(0, countDownSeconds, 0, 1, TimeUnit.SECONDS).
                compose(RxUtil.<Long>ioToMainFlowable()).
                subscribe(next,error,complete,onSubscripe);
    }

}
