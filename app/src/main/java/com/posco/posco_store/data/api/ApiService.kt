package com.posco.posco_store.data.api

import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.Device
import com.posco.posco_store.data.model.User
import retrofit2.http.*


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

    @POST("user-service/user/socialLogin")
    suspend fun checkKakao(@Body user: User) : List<User>

    @GET("app-service/app/all/A/{index}")
    suspend fun getAllApps(@Path("index") index: Int) : List<App>

    @GET("app-service/app/{os}")
    suspend fun getAppList(@Path("os") os: String): List<App>

    @GET("app-service/app/{id}")
    suspend fun getAppDetails(@Path("id") id: String) : App

    @PUT("user-service/device/updateFcm/{fcmName}/{id}")
    suspend fun updateFcmActive(@Path("id") id:Int, @Path("fcmName") fcmName:String, @Body device: Device) : Integer

    @POST("user-service/device/login_1")
    suspend fun addDevice(@Body device: Device) : User

    @GET("user-service/device/device/{userId}")
    suspend fun getDevice(@Path("userId") userId: Int) : Device

}