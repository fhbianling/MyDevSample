package com.bian.util.core;

import android.os.Bundle;

import java.util.Set;

/**
 * author 边凌
 * date 2017/8/16 15:01
 * 类描述：
 */

public class BundleUtil {
    private BundleUtil() {
        throw new UnsupportedOperationException();
    }

    public static String printBundle(Bundle bundle) {
        Set<String> strings = bundle.keySet();
        StringBuilder stringBuilder = new StringBuilder("[");
        for (String string : strings) {
            Object o = bundle.get(string);
            stringBuilder.append(string).append(":").append(o != null ? o.toString() : "").append("; ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
