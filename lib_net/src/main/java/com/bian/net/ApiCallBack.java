package com.bian.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public interface ApiCallBack<T> {

    void onSuccess(@NonNull T t);

    void onFailure(@Nullable Throwable throwable);

}
