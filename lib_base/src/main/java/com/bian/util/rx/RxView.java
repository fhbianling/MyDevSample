package com.bian.util.rx;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * author 边凌
 * date 2017/2/21 11:48
 * desc ${RxView工具}
 * <p>
 * 注：该类方法不依赖于RxBinding这个库，有RxJava支持即可
 */

@SuppressWarnings({"WeakerAccess"})
public class RxView {
    //默认快速点击过滤时间为1000毫秒
    private final static int DEFAULT_INTERVAL = 300;

    private RxView() {
        throw new UnsupportedOperationException();
    }

    /**
     * 快速点击过滤
     */
    public static Disposable throttleFirst(@NonNull final View.OnClickListener listener, @NonNull final View view) {
        return throttleFirst(listener, DEFAULT_INTERVAL, view);
    }

    /**
     * 可设置点击间隔的快速点击过滤
     * <p>
     * 注意1：这里过滤的间隔时间不会累加计算，
     * 例第一下0ms,第二下100ms，第三下200ms，假设间隔时间为200ms，第三下仍然不会生效
     * 因为第三下相对它的上一次间隔为100ms小于200ms，也即必须是距离最近一次的点击200ms后再次点击才能生效
     * 也即点击事件的触发时间不是按照时间线以200ms为间隔等分而触发的。
     * <p>
     * 注意2：若对view设置多次listener会导致该方法失效
     * 注意3：该方法的返回值持有view的强引用
     *
     * @param interval 单位：毫秒
     */
    public static Disposable throttleFirst(@NonNull final View.OnClickListener listener, int interval, @NonNull final View view) {
        return Flowable.create(new ViewClickOnSubscribe(view), BackpressureStrategy.DROP).throttleFirst(interval, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<View>() {
                    @Override
                    public void accept(@NonNull View view) throws Exception {
                        listener.onClick(view);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 可传入多个View的点击过滤器
     *
     * @param listener 监听器
     * @param interval 过滤间隔
     * @param views    view数组
     * @return {@link Subscription}这个引用持有View的强引用
     */
    public static Disposable throttleFirst(@NonNull final View.OnClickListener listener, int interval, @NonNull View... views) {
        return Flowable.create(new ViewsClickOnSubscribe(views), BackpressureStrategy.DROP)
                .throttleFirst(interval, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<View>() {
                    @Override
                    public void accept(@NonNull View view) throws Exception {
                        listener.onClick(view);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public static Disposable throttleFirst(final View.OnClickListener listener, View... views) {
        return throttleFirst(listener, DEFAULT_INTERVAL, views);
    }

    /**
     * 适用于AdapterView的ItemClick的快速点击过滤
     *
     * @param interval    间隔时间
     * @param listener    监听器
     * @param absListView AbsListView
     * @return {@link Subscription}
     */
    public static Disposable itemClick(int interval, @NonNull final AdapterView.OnItemClickListener listener, @NonNull AdapterView absListView) {
        return Flowable.create(new ItemClickOnSubscribe(absListView), BackpressureStrategy.BUFFER)
                .throttleFirst(interval, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<ItemClickOnSubscribe.ItemEvent>() {
                    @Override
                    public void accept(@NonNull ItemClickOnSubscribe.ItemEvent itemEvent) throws Exception {
                        listener.onItemClick(itemEvent.parent, itemEvent.view, itemEvent.position, itemEvent.id);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public static Disposable itemClick(AdapterView.OnItemClickListener listener, AdapterView absListView) {
        return itemClick(DEFAULT_INTERVAL, listener, absListView);
    }

    /**
     * 针对TextView的textChanges事件
     */
    public static Disposable textsChanged(@NonNull final OnTextChangeListener onTextChangeListener, @NonNull TextView textView) {
        return Flowable.create(new TextViewTextOnSubscribe(textView), BackpressureStrategy.DROP)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        onTextChangeListener.onTextChanged(charSequence);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 针对多个TextView的textChange事件，返回参数中CharSequences数组顺序对应于传入的textView参数顺序
     */
    public static Disposable textChanged(@NonNull final OnTextsChangeListener onTextsChangeListener, @NonNull TextView... textViews) {
        return Flowable.create(new TextViewsTextOnSubscribe(textViews), BackpressureStrategy.DROP)
                .subscribe(new Consumer<CharSequence[]>() {
                    @Override
                    public void accept(@NonNull CharSequence[] charSequences) throws Exception {
                        onTextsChangeListener.onTextsChanged(charSequences);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public interface OnTextChangeListener {
        void onTextChanged(CharSequence charSequence);
    }

    public interface OnTextsChangeListener {
        void onTextsChanged(CharSequence[] charSequences);
    }
}
