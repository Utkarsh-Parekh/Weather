package com.example.weatherdata.APIinterfaces

import com.example.weatherdata.weatherdatamodel.Weatherdatainfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Weatherinterface {

    @GET("weather")
    fun getcurrentcityweatherdata(
        @Query("q") q:String,
        @Query("APPID") appid : String,
        @Query("units") units : String
    ): Call<Weatherdatainfo>
}