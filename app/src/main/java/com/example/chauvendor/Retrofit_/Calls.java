package com.example.chauvendor.Retrofit_;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Calls {

    @GET("Snap_cat")
    Call<List<Map<String,Object>>>  getCat();


    @GET("Banklist")
    Call<List<Map<String,Object>>>  get_list_of_bank();
}
