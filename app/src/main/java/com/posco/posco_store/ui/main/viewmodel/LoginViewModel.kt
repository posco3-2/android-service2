package com.posco.posco_store.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.posco.posco_store.data.model.Device
import com.posco.posco_store.data.model.User
import com.posco.posco_store.data.repository.MainRepository
import com.posco.posco_store.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel()
{

    fun fetchLogin(user: User) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.login(user)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }

    fun inputDevice(device: Device) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.regi(device)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }

    fun addDevice(device: Device) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.addDevice(device)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }


    fun checkRegiDevice(id: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.checkRegiDevice(id)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }

    fun updateRegiDevice(id: Int, updateDate: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.updateRegiDevice(id, updateDate)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }

    fun checkKakao(user: User) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.checkKakao(user)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }

}