package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    @GET("user/no")
    suspend fun getUsers(): List<User>

    @POST("user/id")
    suspend fun getUserById(@Body testInt: Int): List<User>

}