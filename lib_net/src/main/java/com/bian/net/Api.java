package com.bian.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的Retrofit2创建类，以及通过该类去调用接口
 */
public class Api {
    private final static String LOG_TAG = "Api";
    private final static HashMap<String, Object> sServiceCache = new HashMap<>();
    static boolean sHttpLoggingEnable = true;
    private static Retrofit sRetrofit;
    private static String baseUrl;
    private static Interceptor[] interceptors;
    private static Converter.Factory[] sFactories;

    private Api() {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置是否输出网络访问日志，注意，当使用Retrofit下载大文件时，通过该方法输出网络访问日志会导致App崩溃
     */
    @SuppressWarnings("unused")
    public static void setHttpLoggingEnable(boolean httpLoggingEnable) {
        Api.sHttpLoggingEnable = httpLoggingEnable;
    }

    /**
     * 设置拦截器，可以为多个，该方法应在{@link #setBaseUrl(String)}前调用
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void setInterceptors(Interceptor... interceptors) {
        Api.interceptors = interceptors;
    }

    /**
     * 设置转换工厂，可以为多个，该方法应在{@link #setBaseUrl(String)}前调用
     * <p>
     * {@link retrofit2.Converter.Factory}这个接口可用于在请求和收到服务器回复的过程中对参数和返回数据进行统一处理
     * 可参考{@link GsonConverterFactory}写
     * <p>
     * 另该类已经按照官方文档建议默认添加了GsonConverterFactory
     */
    @SuppressWarnings("unused")
    public static void setConvertFactory(Converter.Factory... factories) {
        Api.sFactories = factories;
    }

    /**
     * 当调用任何更改了Retrofit设置的方法时，应调用该方法实现对Service的更新
     * 这是因为该类中缓存了Retrofit生成的service
     */
    @SuppressWarnings("WeakerAccess")
    public static void clearServiceCache() {
        sServiceCache.clear();
    }

    /**
     * 创建网络请求接口的代理类
     * <p>
     * 由于该类中包含了其缓存，因此当任何Retrofit的设置更改时，应先调用{@link #clearServiceCache()}
     * 再调用该方法，才能获取到应用了最新设置的对应service
     *
     * @param service Retrofit形式的网络请求对应的接口类
     */
    public static <T> T getService(final Class<T> service) {
        if (sRetrofit == null) {
            throw new NullPointerException("请先调用 setBaseUrl(String)方法");
        }
        Object o = sServiceCache.get(service.getName());
        if (o != null) {
            //noinspection unchecked
            return (T) o;
        } else {
            T t = sRetrofit.create(service);
            sServiceCache.put(service.getName(), t);
            return t;
        }
    }

    /**
     * 释放资源
     */
    @SuppressWarnings("unused")
    public static void release() {
        sRetrofit = null;
    }

    @SuppressWarnings("WeakerAccess")
    public static String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置baseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        setBaseUrl(baseUrl, null);
    }

    /**
     * 设置baseUrl
     *
     * @param builder 如果传入builder则会使用传入的builder来进行构建而非默认的
     */
    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    public static void setBaseUrl(String baseUrl, @Nullable Retrofit.Builder builder) {
        Api.baseUrl = baseUrl;
        Util.i(LOG_TAG, "Base url is setting:" + baseUrl);
        clearServiceCache();
        initApi(builder);
    }

    private static OkHttpClient createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (sHttpLoggingEnable) {
            builder.addNetworkInterceptor(getHttpLoggingInterceptor());
        }

        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        builder.cookieJar(new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);

        return builder.build();
    }

    @NonNull
    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor sHttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                /*在某些情况下可能无法看到返回的json数据，比如服务器做出了特殊设置*/
                Util.printJson(LOG_TAG, message);
            }
        });
        sHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return sHttpLoggingInterceptor;
    }

    private static void initApi(Retrofit.Builder builder) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Gson gson = gsonBuilder.create();

        Retrofit.Builder retrofitBuilder;
        if (builder != null) {
            retrofitBuilder = builder;
            builder.baseUrl(getBaseUrl());
        } else {
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(createClient());
        }


        if (sFactories != null) {
            for (Converter.Factory factory : sFactories) {
                retrofitBuilder.addConverterFactory(factory);
            }
        }

        Api.sRetrofit = retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson))
                                       .build();
    }

}
