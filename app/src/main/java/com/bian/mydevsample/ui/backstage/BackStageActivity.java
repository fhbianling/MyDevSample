package com.bian.mydevsample.ui.backstage;

import android.app.ActivityManager;
import android.os.Bundle;

import com.bian.util.core.L;
import com.bian.util.core.ToastUtil;
import com.bian.mydevsample.R;
import com.bian.mydevsample.base.BaseActivity;

import java.util.List;

/**
 * author 边凌
 * date 2017/12/12 11:04
 * 类描述：
 */

public class BackStageActivity extends BaseActivity {
    @Override
    protected int bindLayoutId() {
        return R.layout.activity_custom_rv;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showToastShort("ready to show toast");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am == null) {
            return;
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                if (runningAppProcess.processName.equals(getApplicationInfo().processName)) {
                    L.d("进入后台");
                    ToastUtil.showToastShort("进入后台,传统方法");
                    ToastUtil.showToastInBackground(this, "进入后台,wm方法", 1000);
                    break;
                }
            }
        }
    }
}
