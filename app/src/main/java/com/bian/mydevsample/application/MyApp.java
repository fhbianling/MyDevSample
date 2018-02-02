package com.bian.mydevsample.application;

import android.app.Application;

import com.bian.util.BaseUtilManager;
import com.bian.mydevsample.BuildConfig;
import com.bian.net.Api;

import static com.bian.mydevsample.consts.Const.BOOK_BASE_URL;

/**
 * author 边凌
 * date 2017/9/20 16:28
 * 类描述：
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaseUtilManager.init(this, BuildConfig.DEBUG);
        Api.setBaseUrl(BOOK_BASE_URL);
    }
}
