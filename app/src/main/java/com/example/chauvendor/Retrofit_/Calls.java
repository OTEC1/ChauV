package com.example.chauvendor.Retrofit_;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Calls {

    @GET("Snap_cat")
    Call<List<Map<String,Object>>>  getCat();


    @GET("Banklist")
    Call<List<Map<String,Object>>>  get_list_of_bank();


    @GET("food_category")
    Call<List<Map<String,Object>>>  get_food_category();

    @POST("SendPasswordRestLink")
    Call<Object> sendRestLink(@Body Map<String,Object> data);

}
