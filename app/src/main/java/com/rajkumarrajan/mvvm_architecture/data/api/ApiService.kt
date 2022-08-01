package com.rajkumarrajan.mvvm_architecture.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rajkumarrajan.mvvm_architecture.data.model.App
import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.LocalDateTime


interface ApiService {

    @GET("user-service/user/allUser")
    suspend fun getUsers(): List<User>

    @POST("user-service/user/id")
    suspend fun getUserById(@Body testInt: Int): List<User>

    @POST("user-service/user/login")
    suspend fun login(@Body user: User): List<User>

    @POST("user-service/device/")
    suspend fun regi(@Body device: Device) : Integer

    @GET("user-service/device/check/{id}")
    suspend fun checkRegiDevice(@Path("id") id:Int) : Integer

    @PUT("user-service/device/update/{id}")
    suspend fun updateRegiDevice(@Path("id") id:Int, @Body updateDate: String) : Integer

    @POST("user-service/user/kakaologin")
    suspend fun checkKakao(@Body user: User) : List<User>

    @GET("app-service/app")
    suspend fun getAllApps() : List<App>

}