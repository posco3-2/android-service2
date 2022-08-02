package com.posco.posco_store.data.repository

import com.posco.posco_store.data.api.ApiService
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.Device
import com.posco.posco_store.data.model.User
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getUsers(): List<User> {
        return apiService.getUsers()
    }

    suspend fun getUserById(testInt:Int): List<User>{
        return apiService.getUserById(testInt)
    }

    suspend fun login(user: User): List<User>{
        return apiService.login(user)
    }

    suspend fun regi(device: Device) : Integer {
        return apiService.regi(device);
    }

    suspend fun checkRegiDevice(id: Int): Integer{
        return apiService.checkRegiDevice(id);
    }

    suspend fun updateRegiDevice(id:Int, updateDate: String): Integer {
        return apiService.updateRegiDevice(id, updateDate);
    }

    suspend fun checkKakao(user: User): List<User> {
        return apiService.checkKakao(user);
    }

    suspend fun getAllApps(): List<App>{
        return apiService.getAllApps();
    }
    //detail 정보 알기 추가
    suspend fun getAppDetails(id: String): App{
        return apiService.getAppDetails(id)
    }

    suspend fun updateFcmActive(id:Int, fcmActive:Int): Integer{
        return apiService.updateFcmActive(id, fcmActive)
    }
}