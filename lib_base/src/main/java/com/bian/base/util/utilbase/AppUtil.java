package com.bian.base.util.utilbase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * 应用工具类
 */
public final class AppUtil {
    private AppUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 指定包名的应用是否安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {

        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageinfo != null;

    }

    /**
     * 指向应用市场指定包名的应用页
     */
    public static void openAppPageInMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 启动指定包名的第三方 app
     */
    public static boolean startAppWithPackageName(Context mContext, String packagename) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        List<ResolveInfo> resolveInfoList = mContext.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveInfoList.iterator().next();
        boolean startSuccess = resolveinfo != null;
        if (startSuccess) {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        }
        return startSuccess;
    }

    public static String getSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (byte aPublicKey : publicKey) {
                String appendString = Integer.toHexString(0xFF & aPublicKey)
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NoSuchAlgorithmException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
