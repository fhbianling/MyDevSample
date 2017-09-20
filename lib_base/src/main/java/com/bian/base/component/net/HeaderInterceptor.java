package com.bian.base.component.net;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author 边凌
 * date 2017/6/28 11:34
 * 类描述：如果需要在请求的头部中添加token或其他值
 * 调用{@link Api#setInterceptors(Interceptor...)}为其设置该类的子类，可以实现在header统一添加token的功能
 * <p>
 * 这只是个示范{@link Interceptor}接口用法的类
 */

public abstract class HeaderInterceptor implements Interceptor {
    protected String TOKEN_KEY;

    public HeaderInterceptor(String keyOfToken) {
        this.TOKEN_KEY = keyOfToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = getToken();
        Request.Builder builder = original.newBuilder();
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(TOKEN_KEY, token);
        }
        return chain.proceed(builder.build());
    }

    public abstract String getToken();
}
