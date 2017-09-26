package com.bian.base.baseclass.baseadapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author 边凌
 * date 2017/9/13 19:57
 * 类描述：通过Retrofit实现的 {@link DataLoader}，已经封装了数据请求过程
 * <p>
 * 使用：在{@link BasePTRAdapter#getDataLoader()}或
 * {@link BaseRecycleViewPTRAdapter#getDataLoader()}方法中返回该类的子类即可
 */

public abstract class RetrofitDataLoader<CallType, DataType>
        implements DataLoader<DataType> {
    @SuppressWarnings("WeakerAccess")
    public final static int ERROR_NO_API_RESPONSE = 1;
    @SuppressWarnings("WeakerAccess")
    public final static int ERROR_NET_THROWABLE = 2;

    /**
     * 返回一个用于请求数据的{@link Call}
     *
     * @see #convertData(Object)
     */
    public abstract Call<CallType> getCall(int pageIndex, int pageSize, LoadType loadType);

    /**
     * 将Call获得的数据类型转换为需要展示的数据类型的集合
     *
     * @see #getCall(int, int, LoadType)
     */
    public abstract List<DataType> convertData(CallType callData);

    @Override
    public void loadData(int pageNum, int pageSize,
                         final DataSetter<DataType> setter,
                         LoadType loadType) {
        Call<CallType> dataCall = getCall(pageNum, pageSize, loadType);
        dataCall.enqueue(new Callback<CallType>() {
            @Override
            public void onResponse(Call<CallType> call, Response<CallType> response) {
                if (response == null) {
                    setter.setFailed(ERROR_NO_API_RESPONSE, "接口返回数据为空");
                } else {
                    List<DataType> dataTypes = convertData(response.body());
                    setter.setLoadedData(dataTypes);
                }
            }

            @Override
            public void onFailure(Call<CallType> call, Throwable t) {
                setter.setFailed(ERROR_NET_THROWABLE, t.getMessage());
            }
        });
    }
}
