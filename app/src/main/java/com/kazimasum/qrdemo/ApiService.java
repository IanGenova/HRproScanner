package com.kazimasum.qrdemo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService<YourRequestModel, YourResponseModel> {
    @Headers("Content-Type: application/json")
    @POST
    Call<YourResponseModel> sendQRCodeData(@Url String url, @Body YourRequestModel requestModel);
}
