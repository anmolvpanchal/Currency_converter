package com.example.currencyconverter.API

import com.example.currencyconverter.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiClient {

    @GET("rates")
    fun getCurrentRate(): Call<ResponseBody>

    @GET("currencies")
    fun getAllCurrencies() : Call<ResponseBody>

    @GET("rates/EUR/{countryCode}")
    fun getSingleRate(@Path("countryCode") countryCode : String) : Call<ResponseBody>

}