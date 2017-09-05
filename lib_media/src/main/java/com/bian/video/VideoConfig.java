package com.bian.video;

/**
 * author 边凌
 * date 2017/6/5 15:10
 * desc ${TODO}
 */

public class VideoConfig {
    /*长度限制,单位秒*/
    private long lengthLimit;
    /*文件大小限制，单位Mb*/
    private long sizeLimit;
    /*请求码*/
    private int requestCode;

    public void setLengthLimit(long lengthLimit) {
        this.lengthLimit = lengthLimit;
    }

    public void setSizeLimit(long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getLengthLimit() {
        return lengthLimit;
    }

    public long getSizeLimit() {
        return sizeLimit*1024*1024;
    }

    public int getRequestCode() {
        return requestCode;
    }
}
