package com.bian.base.util.utilbase;

import android.app.Activity;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;

/**
 * 管理项目中的Activity栈，需要在基类Activity中适时将Activity推入或移除栈
 */
public final class AppActivityManager {

    private static AppActivityManager instance = null;
    private static List<Activity> mActivities = new LinkedList<>();

    private AppActivityManager() {

    }

    public static AppActivityManager getInstance() {
        if (null == instance) {
            synchronized (AppActivityManager.class) {
                if (null == instance) {
                    instance = new AppActivityManager();
                }
            }
        }
        return instance;
    }

    public int size() {
        return mActivities.size();
    }

    public synchronized Activity getForwardActivity() {
        return size() > 0 ? mActivities.get(size() - 1) : null;
    }

    public synchronized void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public synchronized void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }

    public synchronized void clearAll() {
        for (int indexDesc = mActivities.size() - 1; indexDesc > -1; indexDesc--) {
            Activity activity = mActivities.get(indexDesc);
            removeActivity(activity);
            activity.finish();
            indexDesc = mActivities.size();
        }
    }

    public synchronized void clearExcept(Activity activity) {

        for (int indexDesc = mActivities.size() - 1; indexDesc > -1; indexDesc--) {
            Activity activity1 = mActivities.get(indexDesc);
            if (activity1 != activity) {
                removeActivity(activity);
                activity.finish();
            }
            indexDesc = mActivities.size();
        }
    }

    public synchronized void clearExceptTop() {
        if (mActivities.size() < 2) {
            return;
        }
        for (int indexDesc = mActivities.size() - 2; indexDesc > -1; indexDesc--) {
            Activity activity = mActivities.get(indexDesc);
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 判断Activity是否被销毁
     */
    public static boolean isActivityDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        return activity == null || activity.isFinishing();
    }

}
