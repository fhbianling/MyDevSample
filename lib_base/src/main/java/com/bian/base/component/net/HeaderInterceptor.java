package com.bian.base.component.net;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.bian.base.component.net.Api.TOKEN_KEN;

/**
 * author 边凌
 * date 2017/6/28 11:34
 * 类描述：
 */

class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = Api.getTOKEN();
        Request.Builder builder = original.newBuilder();
        if (!TextUtils.isEmpty(token)){
            builder.addHeader(TOKEN_KEN,token);
        }
        return chain.proceed(builder.build());
    }
}
