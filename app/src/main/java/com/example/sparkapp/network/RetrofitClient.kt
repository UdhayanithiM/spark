package com.example.sparkapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    // Your IP address from the previous step
    private const val BASE_URL = "http://10.114.195.149/doctor/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
       // <-- This will now be resolved and will fix the app crash
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiService::class.java)
    }
}