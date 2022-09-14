package com.posco.posco_store.data.api

import com.posco.posco_store.data.model.*
import javax.inject.Inject

class ApiHelper @Inject constructor(private val apiService: ApiService) {
    suspend fun getUsers() = apiService.getUsers()

    suspend fun getUserById(testInt: Int) = apiService.getUserById(testInt)

    suspend fun login(login: LoginDto): LoginResultDto = apiService.login(login)

    suspend fun regi(device: Device) = apiService.regi(device)

    suspend fun checkRegiDevice(id : Int) = apiService.checkRegiDevice(id)

    suspend fun updateRegiDevice(id : Int, updateDate : String) = apiService.updateRegiDevice(id, updateDate)

    suspend fun checkKakao(user: User) = apiService.checkKakao(user)

    suspend fun updateFcmActive(id: Int, fcmName: String, device: Device, authToken: String) = apiService.updateFcmActive(id, fcmName, device, authToken)

    suspend fun addDevice(device: Device) = apiService.addDevice(device)

    suspend fun getDevice(userId: Int, authToken: String) = apiService.getDevice(userId, authToken)
}