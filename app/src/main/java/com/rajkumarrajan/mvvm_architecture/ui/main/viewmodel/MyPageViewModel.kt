package com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.rajkumarrajan.mvvm_architecture.data.repository.MainRepository
import com.rajkumarrajan.mvvm_architecture.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun updateFcmActive(id: Int, fcmActive:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(data = mainRepository.updateFcmActive(id,fcmActive)))
        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }
}