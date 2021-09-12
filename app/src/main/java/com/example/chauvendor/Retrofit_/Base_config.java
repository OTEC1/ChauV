package com.example.chauvendor.Retrofit_;

import com.example.chauvendor.constant.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Base_config {


  static   Retrofit   retrofit;

  public  static  Retrofit getConnection(){
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
              .connectTimeout(30, TimeUnit.SECONDS)
              .readTimeout(30,TimeUnit.SECONDS)
              .writeTimeout(30,TimeUnit.SECONDS)
              .build();

      if (retrofit == null){
          retrofit = new Retrofit.Builder()
                  .baseUrl(Constants.BASE_URL)
                  .client(okHttpClient)
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();
      }

      return  retrofit;
  }
}
