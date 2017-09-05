package com.bian.base.util.utilthrowable;

/**
 * author 边凌
 * date 2017/4/26 16:22
 * desc ${服务器返回数据为空}
 */

public class ResponseBodyNullException extends BaseException{
    public ResponseBodyNullException() {
        super("返回数据为空");
    }
}
