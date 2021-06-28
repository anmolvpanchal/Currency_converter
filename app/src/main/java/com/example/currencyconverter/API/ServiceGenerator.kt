package com.example.currencyconverter.API

import android.os.Build
import com.example.currencyconverter.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceGenerator {

    fun getClient(): Retrofit {

        val mGson: Gson = GsonBuilder()
            .setLenient()
            .create()
        val mHttpLoginInterceptor = HttpLoggingInterceptor()
        //here we will set our log level
        mHttpLoginInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val mOkClient: OkHttpClient.Builder = OkHttpClient.Builder().readTimeout(300,
            TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).connectTimeout(300, TimeUnit.SECONDS)
        // add logging as last interceptor always
        // we will add login interceptor only in case of debug.
        //here firstly we will add our header interseptor
        mOkClient.addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization" , BuildConfig.ApiKey)
                .build()
            chain.proceed(newRequest)
        }

        if (BuildConfig.DEBUG) {
            mOkClient.addInterceptor(mHttpLoginInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.Baseurl)
            .client(mOkClient.build())
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .build()
    }


}