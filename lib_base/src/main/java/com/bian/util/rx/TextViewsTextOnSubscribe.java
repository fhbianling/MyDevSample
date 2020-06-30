package com.bian.util.rx;

import androidx.annotation.NonNull;
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

final class TextViewsTextOnSubscribe implements FlowableOnSubscribe<CharSequence[]> {
    private final TextView[] view;

    TextViewsTextOnSubscribe(@NonNull TextView... view) {
        this.view = view;
    }

    private CharSequence[] getValue() {
        CharSequence[] value = new CharSequence[view.length];
        for (int i = 0; i < view.length; i++) {
            value[i] = view[i].getText();
        }
        return value;
    }

    @Override
    public void subscribe(@NonNull final FlowableEmitter<CharSequence[]> e) throws Exception {
        verifyMainThread();

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                e.onNext(getValue());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        for (TextView textView : view) {
            textView.removeTextChangedListener(watcher);
            textView.addTextChangedListener(watcher);
        }

        e.onNext(getValue());
    }
}
