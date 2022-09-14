package com.posco.posco_store.data.api

import com.posco.posco_store.data.model.*
import retrofit2.http.*


interface ApiService {

    @GET("user-service/user/allUser")
    suspend fun getUsers(): List<User>

    @POST("user-service/user/id")
    suspend fun getUserById(@Body testInt: Int): List<User>

    @POST("user-service/device/login_2")
    suspend fun login(@Body login: LoginDto): LoginResultDto

    @POST("user-service/device/")
    suspend fun regi(@Body device: Device) : Device

    @GET("user-service/device/check/{id}")
    suspend fun checkRegiDevice(@Path("id") id:Int) : Int

    @PUT("user-service/device/update/{id}")
    suspend fun updateRegiDevice(@Path("id") id:Int, @Body updateDate: String) : Int

    @POST("user-service/user/socialLogin")
    suspend fun checkKakao(@Body user: User) : List<User>

    @GET("app-service/app/all/A/{userId}/{index}")
    suspend fun getAppUserList(@Path("userId") userId: Int, @Path("index") index: Int, @Header("Authorization") authToken: String) : List<App>

    @PUT("user-service/device/updateFcm/{fcmName}/{id}")
    suspend fun updateFcmActive(@Path("id") id:Int, @Path("fcmName") fcmName:String, @Body device: Device, @Header("Authorization")  authToken: String) : Int

    @POST("user-service/device/login_1")
    suspend fun addDevice(@Body device: Device) : Login

    @GET("user-service/device/device/{userId}")
    suspend fun getDevice(@Path("userId") userId: Int, @Header("Authorization") authToken: String) : Device



}