package com.bian.net;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author 边凌
 * date 2017/3/15 17:07
 * desc ${参数构建辅助类}
 */

public class ParamBuilder {
    private final static String LOG_TAG = "ParamBuilder";
    private HashMap<String, Object> options;

    private ParamBuilder() {
        options = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public static ParamBuilder create() {
        log("paramBuild:create");
        return new ParamBuilder();
    }

    /**
     * 参数输出日志
     */
    private static void log(String msg) {
        Util.v(LOG_TAG, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static RequestBody createImageBody(String file) {
        return RequestBody.create(MediaType.parse("image/*"), new File(file));
    }

    @SuppressWarnings("unused")
    public static RequestBody createFileBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    @SuppressWarnings("WeakerAccess")
    public static Map<String, RequestBody> createImageBodyMap(List<String> data, String key) {
        HashMap<String, RequestBody> body = new HashMap<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                body.put(key + "\";filename=\"" + key + i + ".jpg",
                         ParamBuilder.createImageBody(data.get(i)));
            }
        }
        return body;
    }

    @SuppressWarnings("unused")
    public static Map<String, RequestBody> createImageBodyMap(String key, String... imgUrl) {
        return createImageBodyMap(Arrays.asList(imgUrl), key);
    }

    /**
     * 如果value 不是RequestBody形式，则转换为字符串填入参数
     *
     * @param key   key
     * @param value value
     * @return ParamBuilder
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public ParamBuilder put(String key, Object value) {
        if (!(value instanceof RequestBody)) {
            try {
                log("paramBuild:(" + key + "," + value + ")");
                options.put(key, String.valueOf(value));
                return this;
            } catch (Exception e) {
                log("ParamBuilder put failed:" + e.getMessage());
            }
        } else {
            options.put(key, value);
        }
        return this;
    }

    public HashMap<String, Object> build() {
        log("paramBuild:build");
        return new HashMap<>(options);
    }

    @SuppressWarnings("unused")
    public ParamBuilder putImageBody(String key, String filePath) {
        /*"picture\"; filename=\"picture.jpg"*/
        put(key + "\";filename=\"" + key + ".jpg", createImageBody(filePath));
        return this;
    }

}
