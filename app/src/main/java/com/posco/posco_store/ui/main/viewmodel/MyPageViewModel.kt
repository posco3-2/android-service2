package com.posco.posco_store.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.posco.posco_store.data.repository.MainRepository
import com.posco.posco_store.utils.Resource
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