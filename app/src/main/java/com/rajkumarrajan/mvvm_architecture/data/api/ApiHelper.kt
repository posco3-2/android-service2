package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import javax.inject.Inject

class ApiHelper @Inject constructor(private val apiService: ApiService) {
    suspend fun getUsers() = apiService.getUsers()

    suspend fun getUserById(testInt: Int) = apiService.getUserById(testInt)

    suspend fun login(user: User ) = apiService.login(user)

    suspend fun regi(device: Device) = apiService.regi(device)
}