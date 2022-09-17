package com.posco.posco_store

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.posco.posco_store.utils.SharedPreference
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    companion object {
        lateinit var sharedPreference: SharedPreference
    }


    override fun onCreate() {
        super.onCreate()
        sharedPreference = SharedPreference(applicationContext)

        // Kakao SDK 초기화
        KakaoSdk.init(this, "563d01ea237202c1de004f03316bf42c")
    }

}