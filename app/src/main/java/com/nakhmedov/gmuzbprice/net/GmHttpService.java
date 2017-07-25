package com.nakhmedov.gmuzbprice.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates
 */

public interface GmHttpService {

    @GET("xml")
    Call<ResponseBody> listCurrency();
}
