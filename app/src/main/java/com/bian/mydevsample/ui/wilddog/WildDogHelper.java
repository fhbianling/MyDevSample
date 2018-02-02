package com.bian.mydevsample.ui.wilddog;

import android.content.Context;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

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
