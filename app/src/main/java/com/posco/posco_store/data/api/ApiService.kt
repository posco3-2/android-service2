package com.posco.posco_store.data.api

import com.posco.posco_store.data.model.*
import retrofit2.http.*


interface ApiService {

    @POST("user-service/device/login_2")
    suspend fun login(@Body login: LoginDto): LoginResultDto

    @GET("app-service/app/all/A/{userId}/{index}")
    suspend fun getAppUserList(@Path("userId") userId: Int, @Path("index") index: Int, @Header("Authorization") authToken: String) : List<App>

    @PUT("user-service/device/updateFcm/{fcmName}/{id}")
    suspend fun updateFcmActive(@Path("id") id:Int, @Path("fcmName") fcmName:String, @Body device: Device, @Header("Authorization")  authToken: String) : Int

    @GET("user-service/device/device/{userId}")
    suspend fun getDevice(@Path("userId") userId: Int, @Header("Authorization") authToken: String) : Device

    @GET("user-service/device/fcmInfo/{userId}")
    suspend fun getDeviceFcm(@Path("userId") userId: Int, @Header("Authorization") authToken: String) : DeviceFcm

}