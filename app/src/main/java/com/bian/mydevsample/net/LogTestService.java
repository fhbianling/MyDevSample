package com.bian.mydevsample.net;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author 边凌
 * date 2017/9/20 16:58
 * 类描述：
 */

public interface LogTestService {
    @POST("gwgj/user/getUserInfo")
    Call<Object> test(@Query("userId") String userId);
}
