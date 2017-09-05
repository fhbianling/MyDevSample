package com.bian.base.util.utilrx.rxview;

import android.support.annotation.NonNull;
import android.view.View;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

/**
 * author 边凌
 * date 2017/3/23 14:00
 * desc ${多个view}
 */

final class ViewsClickOnSubscribe implements FlowableOnSubscribe<View>{
    private final View[] views;

    ViewsClickOnSubscribe(View... view) {
        this.views = view;
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<View> e) throws Exception {
        verifyMainThread();
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.onNext(v);
            }
        };

        for (View view : views) {
            view.setOnClickListener(listener);
        }
    }
}
