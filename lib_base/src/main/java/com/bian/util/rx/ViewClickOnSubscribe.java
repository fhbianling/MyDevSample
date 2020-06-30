package com.bian.util.rx;

import androidx.annotation.NonNull;
import android.view.View;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

/**
 * author 边凌
 * date 2017/4/1 9:22
 * desc ${单个view}
 */

final class ViewClickOnSubscribe implements FlowableOnSubscribe<View> {
    private final View view;

    ViewClickOnSubscribe(View view) {
        this.view = view;
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<View> e) throws Exception {
        verifyMainThread();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.onNext(v);
            }
        });
    }
}
