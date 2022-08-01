package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager

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

        binding.textView6.text  = getPhoneNumber()
        binding.textView8.text = getDeviceId()
        binding.textView12.text = LoginActivity.prefs.getString("userName","No")



        binding.button2.setOnClickListener {
            finish()
        }

    }

    // android device id 확인
    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }


    // android devcie os 확인
    fun getDeviceOs(): String {
        return Build.VERSION.RELEASE.toString()
    }

    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        var tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.line1Number
    }



}

