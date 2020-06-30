package com.bian.util.core;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;

/**
 * author 边凌
 * date 2017/8/25 16:55
 * 类描述：
 */

public class VersionUtil {
    private VersionUtil(){
      throw new UnsupportedOperationException();
    }

    /**
     * 获取当前版本的版本号
     */
    public static int getVersionCode(@NonNull Context context) throws PackageManager.NameNotFoundException {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                0);
        return info.versionCode;
    }

    /**
     * 获取当前版本的版本名
     */
    public static String getVersionName(@NonNull Context context) throws PackageManager.NameNotFoundException {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        return info.versionName;
    }


    /**
     * 从指定Url下载新版本
     */
    public static void downLoadAPK(Context context, @NonNull String url, String apkName) {

        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url can't be empty");
        }

        try {
            String serviceString = Context.DOWNLOAD_SERVICE;
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(serviceString);

            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + apkName + "/", apkName + ".apk");
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + apkName + "/", apkName + ".apk");
            long enqueue = downloadManager.enqueue(request);
        } catch (Exception exception) {
            ToastUtil.showToastShort("更新失败");
        }

    }
}
