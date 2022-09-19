package com.posco.posco_store.data.repository

import com.posco.posco_store.MainApplication
import com.posco.posco_store.data.api.ApiService
import com.posco.posco_store.data.model.*
import com.posco.posco_store.ui.main.view.LoginActivity
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {

    val authToken = "Bearer "+ MainApplication.sharedPreference.token //LoginActivity.prefs.getString("token","")

    suspend fun login(login: LoginDto): LoginResultDto {
        return apiService.login(login)
    }

    suspend fun updateFcmActive(id:Int, fcmName: String, device:Device ): Int{
        return apiService.updateFcmActive(id, fcmName, device, authToken)
    }

    suspend fun getDevice(userId : Int) : Device{
        return apiService.getDevice(userId, authToken)
    }

    //appList 가져오는것
    suspend fun getAppUserList(userId: Int, index: Int):List<App>{
        return apiService.getAppUserList(userId, index, authToken)
    }

    suspend fun getFcmActive(userId: Int): DeviceFcm{
        return apiService.getDeviceFcm(userId, authToken)
    }
}