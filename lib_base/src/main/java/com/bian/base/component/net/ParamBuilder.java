package com.bian.base.component.net;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bian.base.util.utilbase.L;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * author 边凌
 * date 2017/3/15 17:07
 * desc ${参数构建辅助类}
 */

public class ParamBuilder {
    private final static String LOG_TAG = "ParamBuilder";
    private final static boolean DEBUG = Api.getDebug();
    private HashMap<String, Object> options;

    private ParamBuilder() {
        options = new HashMap<>();
    }

    public static ParamBuilder create() {
        log("paramBuild:create");
        return new ParamBuilder();
    }

    /**
     * 参数输出日志
     * debug属性取决于{@link Api#setDebug(boolean)}的debug设置
     */
    private static void log(String msg) {
        if (DEBUG) {
            L.v(LOG_TAG, msg);
        }
    }

    public static RequestBody createImageBody(String file) {
        return RequestBody.create(MediaType.parse("image/*"), new File(file));
    }

    public static RequestBody createFileBody(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    public static Map<String, RequestBody> createImageBodyMap(List<String> data, String key) {
        HashMap<String, RequestBody> body = new HashMap<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                body.put(key + "\";filename=\"" + key + i + ".jpg", ParamBuilder.createImageBody(data.get(i)));
            }
        }
        return body;
    }

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

    public ParamBuilder putImageBody(String key, String filePath) {
        /*"picture\"; filename=\"picture.jpg"*/
        put(key + "\";filename=\"" + key + ".jpg", createImageBody(filePath));
        return this;
    }

}
