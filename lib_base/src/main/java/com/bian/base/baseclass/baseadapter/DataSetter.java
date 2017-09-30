package com.bian.base.baseclass.baseadapter;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * author 边凌
 * date 2017/9/20 9:54
 * 类描述：
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface DataSetter<T> {

    void setLoadedData(List<T> data);

    void setFailed(@Nullable String dataErrorMsg);

    void setFailed(int errorCode, @Nullable String dataErrorMsg);
}
