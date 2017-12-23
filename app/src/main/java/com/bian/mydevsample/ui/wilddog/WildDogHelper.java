package com.bian.mydevsample.ui.wilddog;

import android.content.Context;
import android.text.TextUtils;

import com.bian.base.util.utilbase.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author 边凌
 * date 2017/12/13 14:56
 * 类描述：
 */

class WildDogHelper {
    private static final String WILDDOG_APP_ID = "wd2219435489whuibs";
    private static final String WILDDOG_BASE_URL = "https://%s.wilddogio.com";
    private static WildDogHelper sInstance;
    private SyncReference ref;
    private WildDogHelper(Context context) {
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl(String.format(
                WILDDOG_BASE_URL,
                WILDDOG_APP_ID)).build();
        WilddogApp.initializeApp(context, options);
        ref = WilddogSync.getInstance().getReference();
    }

    static WildDogHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WildDogHelper.class) {
                if (sInstance == null) {
                    sInstance = new WildDogHelper(context);
                }
            }
        }
        return sInstance;
    }

    public SyncReference getRef() {
        return ref;
    }
}
