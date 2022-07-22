package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.databinding.ActivityMypageBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView6.text = getDeviceId()
        binding.textView8.text  = getDeviceModel()
        binding.textView12.text = getDeviceOs()
        binding.textView11.text = getAppVersion()

    }

    // android device id 확인
    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }


    // android devcie model 확인
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    // android devcie os 확인
    fun getDeviceOs(): String {
        return Build.VERSION.RELEASE.toString()
    }

    // android app version 확인
    fun getAppVersion(): String {
        val info: PackageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        return info.versionName
    }

}

