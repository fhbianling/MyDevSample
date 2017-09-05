package com.bian.base.util.utilbase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * author 边凌
 * date 2017/8/25 16:48
 * 类描述：
 */

public class IntentUtil {
    private IntentUtil(){
      throw new UnsupportedOperationException();
    }

    /**
     * 判断应响应的Intent是否存在
     *
     * @param context context
     * @param action  action
     * @return 某个Intent是否存在
     */
    public static boolean isIntentExisting(Context context, String action) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null && resolveInfo.size() > 0;
    }

    /**
     * 发送一个打电话的意图
     */
    public static void sendIntentOfCall(Activity activity, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 0x13);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        activity.startActivity(intent);
    }
}
