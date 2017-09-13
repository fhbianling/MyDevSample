package com.bian.base.component.net;

import android.text.TextUtils;

import com.bian.base.util.utilbase.L;
import com.bian.base.util.utilbase.SharedPrefUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的Retrofit2创建类，以及通过该类去调用接口
 */
public class Api {
    final static String TOKEN_KEN = "token";
    private final static String LOG_TAG = "Api";
    private static Retrofit sRetrofit;
    private static boolean debug;
    private static String baseUrl;
    private static String TOKEN;
    private static boolean sHttpLoggingEnable = true;

    private Api() {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置是否输出网络访问日志，注意，当使用Retrofit下载大文件时，通过Retrofit输出网络访问日志会导致App崩溃
     * 调用该方法后，除非调用{@link #setBaseUrl(String)}或{@link #createService(Class)}，
     * 否则该setter所带来的属性不会实时更新到Api设置中
     */
    public static void setHttpLoggingEnable(boolean httpLoggingEnable) {
        Api.sHttpLoggingEnable = httpLoggingEnable;
    }

    public static String getTOKEN() {
        if (!SharedPrefUtil.isReady()){
            L.e("Cannot use token function,because of BaseUtilManager.init(...) isn't called.");
            return "";
        }
        if (TextUtils.isEmpty(TOKEN)) {
            TOKEN = SharedPrefUtil.config().getString(TOKEN_KEN);
        }
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        if (!SharedPrefUtil.isReady()){
            L.e("Cannot use token function,because of BaseUtilManager.init(...) isn't called.");
            return;
        }
        Api.TOKEN = TOKEN;
        SharedPrefUtil.config().putString(TOKEN_KEN, TOKEN);
    }

    static boolean getDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Api.debug = debug;
    }

    private static OkHttpClient createClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.printJson(message);
            }
        });
        httpLoggingInterceptor.setLevel(
                debug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE
        );

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (sHttpLoggingEnable) {
            builder.addNetworkInterceptor(httpLoggingInterceptor);
        }
        builder.addInterceptor(new HeaderInterceptor());

        builder.
                cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).
                readTimeout(60, TimeUnit.SECONDS).
                connectTimeout(60, TimeUnit.SECONDS);

        return builder.build();
    }

    private static void initApi() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Gson gson = builder.create();

        Api.sRetrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(createClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static <T> T createService(final Class<T> service) {
        if (sRetrofit == null) {
            initApi();
        }
        return sRetrofit.create(service);
    }

    public static void release() {
        sRetrofit = null;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        Api.baseUrl = baseUrl;
        L.d(LOG_TAG, "Base url is setting:" + baseUrl);
        initApi();
    }


    public static void resetIPinfo(String ip) {
        setBaseUrl("http://" + ip + "/");
    }
}
