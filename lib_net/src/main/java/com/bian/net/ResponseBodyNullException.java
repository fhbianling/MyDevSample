package com.bian.net;

/**
 * author 边凌
 * date 2017/4/26 16:22
 * desc ${服务器返回数据为空}
 */

@SuppressWarnings("WeakerAccess")
public class ResponseBodyNullException extends Exception{
    public ResponseBodyNullException() {
        super("返回数据为空");
    }
}
