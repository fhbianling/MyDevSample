package com.bian.base.util.utilrx.rxview;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

/**
 * author 边凌
 * date 2017/4/12 9:20
 * desc ${TextView文字变化}
 */

 final class TextViewTextOnSubscribe implements FlowableOnSubscribe<CharSequence> {
    private final TextView view;

    TextViewTextOnSubscribe(TextView view) {
        this.view = view;
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<CharSequence> e) throws Exception {
        verifyMainThread();

        final TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                e.onNext(s);
            }

            @Override public void afterTextChanged(Editable s) {
            }
        };

        view.removeTextChangedListener(watcher);

        view.addTextChangedListener(watcher);

        // Emit initial value.
        e.onNext(view.getText());
    }
}
