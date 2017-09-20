package com.bian.mydevsample.net;

import com.bian.mydevsample.bean.BookRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * author 边凌
 * date 2017/9/13 20:32
 * 类描述：
 */

public interface BookService {
    @GET("/v2/book/search")
    Call<BookRequest> getBookList(@Query("q")String searchCondition, @Query("start")int start, @Query("count")int count);
}
