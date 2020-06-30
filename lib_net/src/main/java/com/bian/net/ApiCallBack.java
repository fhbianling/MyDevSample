package com.bian.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public interface ApiCallBack<T> {

    void onSuccess(@NonNull T t);

    void onFailure(@Nullable Throwable throwable);

}
