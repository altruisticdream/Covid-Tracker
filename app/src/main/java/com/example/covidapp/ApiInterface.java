package com.example.covidapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    String Base_url="https://corona.lmao.ninja/v2/";
    @GET("countries")
    Call<List<ModelClass>> getCountryData();

}
