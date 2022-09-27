package com.example.appark.webservices;

import com.example.appark.Model.directionresponse.DirectionResponseModel;
import com.example.appark.Model.googleresponse.GoogleResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<GoogleResponseModel> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}
