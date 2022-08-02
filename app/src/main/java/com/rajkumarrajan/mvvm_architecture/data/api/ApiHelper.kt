package com.rajkumarrajan.mvvm_architecture.data.api

import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import retrofit2.http.Body
import java.time.LocalDateTime
import javax.inject.Inject

class ApiHelper @Inject constructor(private val apiService: ApiService) {
    suspend fun getUsers() = apiService.getUsers()

    suspend fun getUserById(testInt: Int) = apiService.getUserById(testInt)

    suspend fun login(user: User ) = apiService.login(user)

    suspend fun regi(device: Device) = apiService.regi(device)

    suspend fun checkRegiDevice(id : Int) = apiService.checkRegiDevice(id)

    suspend fun updateRegiDevice(id : Int, updateDate : String) = apiService.updateRegiDevice(id, updateDate)

    suspend fun checkKakao(user: User) = apiService.checkKakao(user)

    suspend fun getAllApps() = apiService.getAllApps()

    suspend fun updateFcmActive(id: Int, fcmActive: Int) = apiService.updateFcmActive(id, fcmActive)
}