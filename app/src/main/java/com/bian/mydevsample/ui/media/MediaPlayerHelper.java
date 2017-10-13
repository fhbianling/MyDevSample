package com.bian.mydevsample.ui.media;

/**
 * author 边凌
 * date 2017/10/13 16:58
 * 类描述：
 */

public class MediaPlayerHelper {
    /**
     * 把毫秒转换成：1:20:30这里形式
     */
    public static String formatTime(int millSeconds) {
        int totalSeconds = millSeconds / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
