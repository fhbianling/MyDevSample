package com.bian.base.util.utilbase;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author 边凌
 * date 2017/6/30 17:53
 * 类描述：
 */

public class FileUtil {
    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    public static void saveBitmap(String fileDir, String fileName, Bitmap bitmap) {
        File f = new File(fileDir, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 生成一个二级缓存菜单目录
     *
     * @param cacheDirName       一级缓存菜单目录
     * @param secondCacheDirName 二级缓存菜单目录
     * @param makeFile           是否创建这个文件夹
     * @return 二级缓存菜单目录
     */
    public static String generateCacheFilePath(String cacheDirName, String secondCacheDirName, boolean makeFile) {
        String rootPath = Environment.getExternalStorageDirectory().getPath() + File.separator + cacheDirName;
        String filePath = rootPath + File.separator + secondCacheDirName;
        if (makeFile) {
            new File(filePath).mkdirs();
        }
        return filePath;
    }

    public static boolean checkFileExist(String resultPath) {
        return !TextUtils.isEmpty(resultPath) && new File(resultPath).exists();
    }

    public static boolean deleteFileOrDir(File file) {
        if (file.isFile() || file.list().length == 0) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFileOrDir(file1);
                file1.delete();
            }

            if (file.exists()) {
                //如果文件本身就是目录 ，就要删除目录
                return file.delete();
            }
        }
        return !file.exists();
    }

    public static String getMimeType(String url){
        String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(url);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            int size2;
            if (fileList != null) {
                size2 = fileList.length;
                for (int i = 0; i < size2; i++) {
                    if (fileList[i].isDirectory()) {
                        size = size + getFileSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
