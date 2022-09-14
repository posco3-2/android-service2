package com.posco.posco_store.data.repository

import com.posco.posco_store.data.api.ApiService
import com.posco.posco_store.data.model.*
import com.posco.posco_store.ui.main.view.LoginActivity
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {

    val authToken = "Bearer"+ LoginActivity.prefs.getString("token","")

    suspend fun getUsers(): List<User> {
        return apiService.getUsers()
    }

    suspend fun getUserById(testInt:Int): List<User>{
        return apiService.getUserById(testInt)
    }

    suspend fun login(login: LoginDto): LoginResultDto {
        return apiService.login(login)
    }

    suspend fun regi(device: Device) : Device {
        return apiService.regi(device)
    }

    suspend fun checkRegiDevice(id: Int): Int{
        return apiService.checkRegiDevice(id)
    }

    suspend fun updateRegiDevice(id:Int, updateDate: String): Int {
        return apiService.updateRegiDevice(id, updateDate)
    }

    suspend fun checkKakao(user: User): List<User> {
        return apiService.checkKakao(user)
    }


    suspend fun updateFcmActive(id:Int, fcmName: String, device:Device ): Int{
        return apiService.updateFcmActive(id, fcmName, device, authToken)
    }

    suspend fun addDevice(device: Device): Login {
        return apiService.addDevice(device)
    }

    suspend fun getDevice(userId : Int) : Device{
        return apiService.getDevice(userId, authToken)
    }

    //appList 가져오는것
    suspend fun getAppUserList(userId: Int, index: Int):List<App>{
        return apiService.getAppUserList(userId, index, authToken)
    }
}