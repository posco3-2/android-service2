package com.rajkumarrajan.mvvm_architecture.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @GET("user/allUser")
    suspend fun getUsers(): List<User>

    @POST("user/id")
    suspend fun getUserById(@Body testInt: Int): List<User>

    @POST("user/login")
    suspend fun login(@Body user: User): List<User>

    @POST("device/regi")
    suspend fun regi(@Body device: Device) : Integer

}