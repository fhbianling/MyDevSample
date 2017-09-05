package com.bian.base.component.net;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

import com.bian.base.util.utilbase.AppActivityManager;
import com.bian.base.util.utilbase.AppUtil;
import com.bian.base.util.utilthrowable.ResponseBodyNullException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author 边凌
 * date 2017/4/18 10:23
 * desc ${对网络请求封装的工具类，可以考虑加入dialog和参数处理等的支持}
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ApiCall {
    private WeakReference<Context> contextWeakReference;
    private LoadingDialog loadingDialog;
    private boolean showLoadingDialog;

    public ApiCall(Context context) {
        this(context, true);
    }

    public ApiCall(Context context, boolean showLoadingDialog) {
        this.showLoadingDialog = showLoadingDialog;
        if (context != null && showLoadingDialog) {
            this.loadingDialog = new LoadingDialog(context);
        }
        contextWeakReference = new WeakReference<>(context);
    }

    public void setShowLoadingDialog(boolean showLoadingDialog) {
        this.showLoadingDialog = showLoadingDialog;
    }

    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

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

    public <T> void enqueue(boolean showLoadingDialog,Call<T> call, final ApiCallBack<T> apiCallBack) {
        this.showLoadingDialog=showLoadingDialog;
        enqueue(call,apiCallBack);
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
            if (context != null && context instanceof Activity && !AppActivityManager.isActivityDestroyed((Activity) context)) {
                loadingDialog.show();
            }
        }
    }

    private void hide() {
        if (!showLoadingDialog) {
            return;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void release(){
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
