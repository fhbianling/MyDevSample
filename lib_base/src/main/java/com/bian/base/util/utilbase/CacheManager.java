package com.bian.base.util.utilbase;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import static com.bian.base.util.utilbase.FileUtil.deleteFileOrDir;
import static com.bian.base.util.utilbase.FileUtil.getFileSize;
import static com.bian.base.util.utilbase.UnitUtil.getFormatSize;

/**
 * 缓存清理工具类
 */
public final class CacheManager {
    private CacheManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取缓存目录总大小
     *
     * @param context   context
     * @param extraPath 更多需要被计算的文件夹
     * @return 缓存目录总大小
     * @throws Exception 获取当前缓存
     */
    public static String getTotalCacheSize(Context context, String... extraPath) throws Exception {
        long cacheSize = getFileSize(context.getCacheDir());

        if (isExternalCacheAvailable()) {
            cacheSize += getFileSize(context.getExternalCacheDir());
        }

        if (extraPath != null) {
            for (String s : extraPath) {
                cacheSize += getFileSize(new File(s));
            }
        }
        return getFormatSize(cacheSize);
    }

    /**
     * @param context 删除缓存
     */
    public static boolean clearAllCache(Context context, String... extraPth) {
        return clearCacheDir(context)
                && clearExternalCacheDir(context) && clearExtraCacheDir(extraPth);
    }

    private static boolean clearCacheDir(Context context) {
        return deleteFileOrDir(context.getCacheDir());
    }

    private static boolean clearExternalCacheDir(Context context) {
        return isExternalCacheAvailable() && deleteFileOrDir(context.getExternalCacheDir());
    }

    private static boolean clearExtraCacheDir(String... extraPth) {
        boolean deleteExtra = true;
        if (extraPth != null) {
            for (String s : extraPth) {
                boolean deleteExtraPath = deleteFileOrDir(new File(s));
                if (!deleteExtraPath) {
                    deleteExtra = false;
                }
            }
        }
        return deleteExtra;
    }

    private static boolean isExternalCacheAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

}
