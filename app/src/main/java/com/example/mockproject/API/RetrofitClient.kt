package com.example.mockproject.API

import com.example.mockproject.Constant.APIConstant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class RetrofitClient {
    private lateinit var retrofit: Retrofit

    fun getRetrofitInstance(): Retrofit{
        retrofit = Retrofit.Builder()
            .baseUrl(APIConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}