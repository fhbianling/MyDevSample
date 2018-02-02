package com.bian.util.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2017/4/5 17:59
 * desc ${权限帮助类}
 */

@SuppressWarnings("WeakerAccess")
public final class PermissionUtil {
    private final static int REQUEST_CODE = 0X999;

    private PermissionUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * 扫描应用所需权限并申请没有的权限
     *
     * @param activity activity
     */
    public static void checkAllPermission(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        String packageName = activity.getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestPermissions = packageInfo.requestedPermissions;
            checkAndRequestPermissions(activity, requestPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static String[] filterDeniedPermissions(Activity activity, String[] permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            int code = PermissionChecker.checkSelfPermission(activity, permission);
            if (code != PermissionChecker.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    public static void checkAndRequestPermissions(Activity activity, String[] permissions) {
        String[] deniedPermissions = filterDeniedPermissions(activity, permissions);
        if (deniedPermissions.length != 0) {
            ActivityCompat.requestPermissions(activity, deniedPermissions, REQUEST_CODE);
        }
    }

    public static boolean hasPermission(Context context,String ...permission){
        for (String s : permission) {
            if (PermissionChecker.checkSelfPermission(context,s)!=PermissionChecker.PERMISSION_DENIED){
                L.d("hasPermission,false:"+s);
                return false;
            }
        }
        return true;
    }
}
