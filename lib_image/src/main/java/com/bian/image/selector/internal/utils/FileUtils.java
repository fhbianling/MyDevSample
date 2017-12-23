package com.bian.image.selector.internal.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @author yuyh.
 * @date 16/4/9.
 */
public class FileUtils {

    /**
     * 创建根缓存目录
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = null;
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cacheRootPath = externalCacheDir.getPath();
            }
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 递归创建文件夹
     *
     * @return 创建失败返回""
     */
    @SuppressWarnings({"UnusedReturnValue", "ResultOfMethodCallIgnored"})
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                i("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                i("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 创建文件
     *
     * @return 创建失败返回""
     */
    @SuppressWarnings("UnusedReturnValue")
    public static String createFile(File file) {
        try {
            if (!file.getParentFile().exists()) {
                createDir(file.getParentFile().getAbsolutePath());
            }
            boolean newFile = file.createNewFile();
            String absolutePath = file.getAbsolutePath();
            i("创建文件:" + absolutePath + (newFile ? "成功" : "失败"));
            return absolutePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void i(String msg) {
        Log.i("ImageSelector", msg);
    }
}
