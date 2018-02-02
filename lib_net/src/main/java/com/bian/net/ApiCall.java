package com.bian.net;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bian.net.Util.isActivityDestroyed;

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

    public ApiCall() {
        this(null, false);
    }

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
        showLoadingDialog();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                hideLoadingDialog();
                if (apiCallBack == null) {
                    return;
                }

                if (!isResponseNull(response)) {
                    //这里经过检测后body不可能为Null
                    //noinspection ConstantConditions
                    apiCallBack.onSuccess(response.body());
                } else {
                    onFailure(call, new ResponseBodyNullException());
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                hideLoadingDialog();
                if (apiCallBack != null) {
                    apiCallBack.onFailure(t);
                }
            }
        });
    }

    /**
     * @param showLoadingDialog 是否加载显示弹窗
     */
    public <T> void enqueue(boolean showLoadingDialog, Call<T> call,
                            final ApiCallBack<T> apiCallBack) {
        this.showLoadingDialog = showLoadingDialog;
        enqueue(call, apiCallBack);
    }

    /**
     * 当使用了LoadingDialog时，应该在{@link Activity#onDestroy()}中调用该方法以防止窗体泄露
     */
    public void release() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private <T> boolean isResponseNull(Response<T> response) {
        return response == null || response.body() == null;
    }

    private void showLoadingDialog() {
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
        return context != null && context instanceof Activity && !isActivityDestroyed(
                (Activity) context);
    }


    private void hideLoadingDialog() {
        if (!showLoadingDialog) {
            return;
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
