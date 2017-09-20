package com.bian.base.component.net;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bian.base.util.utilbase.AppActivityManager;
import com.bian.base.util.utilthrowable.ResponseBodyNullException;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author 边凌
 * date 2017/4/18 10:23
 * desc ${对网络请求封装的工具类，可以考虑加入dialog和参数处理等的支持}
 * <p>
 * 可将LoadingDialog替换为项目对应的LoadingDialog
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ApiCall {
    private WeakReference<Context> contextWeakReference;
    private Dialog loadingDialog;
    private boolean showLoadingDialog;

    public ApiCall(Context context) {
        this(context, true);
    }

    public ApiCall(Context context, boolean showLoadingDialog) {
        this.showLoadingDialog = showLoadingDialog;
        if (context != null && showLoadingDialog) {
            this.loadingDialog = createLoadingDialog(context);
        }
        contextWeakReference = new WeakReference<>(context);
    }

    /**
     * 重写该方法以替换为项目自定义的数据加载弹窗
     */
    @NonNull
    private Dialog createLoadingDialog(Context context) {
        return new LoadingDialog(context);
    }

    /**
     * @param showLoadingDialog 是否在数据请求过程中显示加载弹窗
     */
    public void setShowLoadingDialog(boolean showLoadingDialog) {
        this.showLoadingDialog = showLoadingDialog;
    }

    public Dialog getLoadingDialog() {
        return loadingDialog;
    }

    /**
     * 请求数据
     *
     * @param <T> 代表返回数据的泛型
     */
    public <T> void enqueue(Call<T> call, final ApiCallBack<T> apiCallBack) {
        if (call.isExecuted()) return;
        show();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (apiCallBack == null) {
                    hide();
                    return;
                }

                if (!checkResponseNull(response)) {
                    apiCallBack.onSuccess(response.body());
                } else {
                    apiCallBack.onFailure(new ResponseBodyNullException());
                }

                hide();
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (apiCallBack != null) {
                    apiCallBack.onFailure(t);
                }
                hide();
            }
        });
    }

    /**
     * @param showLoadingDialog 是否加载显示弹窗
     */
    public <T> void enqueue(boolean showLoadingDialog, Call<T> call, final ApiCallBack<T> apiCallBack) {
        this.showLoadingDialog = showLoadingDialog;
        enqueue(call, apiCallBack);
    }

    /**
     * 当使用了LoadingDialog时，应该在{@link Activity#onDestroy()}中调用该方法以防止窗体泄露
     */
    public void release() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private <T> boolean checkResponseNull(Response<T> response) {
        return response == null || response.body() == null;
    }

    private void show() {
        if (!showLoadingDialog) {
            return;
        }
        Context context = contextWeakReference.get();
        if (loadingDialog != null) {
            if (canShowLoadingDialog(context)) {
                loadingDialog.show();
            }
        }
    }

    private boolean canShowLoadingDialog(Context context) {
        return context != null && context instanceof Activity && !AppActivityManager.isActivityDestroyed((Activity) context);
    }

    private void hide() {
        if (!showLoadingDialog) {
            return;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
