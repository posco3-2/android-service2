package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.databinding.ActivityMypageBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.DetailViewModel
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.MainViewModel
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.MyPageViewModel
import com.rajkumarrajan.mvvm_architecture.utils.MySharedPreferences
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    private val myPageViewModel: MyPageViewModel by viewModels()
    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView6.text  = getPhoneNumber()
        binding.textView8.text = getDeviceId()
        binding.textView12.text = LoginActivity.prefs.getString("userName","No")

        val id: Int = LoginActivity.prefs.getString("id","No").toInt()

        binding.button2.setOnClickListener {
            finish()
        }

        binding.switch1.setOnCheckedChangeListener{CompoundButton, onSwitch ->

            //  스위치가 켜지면
            if (onSwitch){
                Toast.makeText(this, "switch on", Toast.LENGTH_SHORT).show()
                updateFcmActive(id, fcmActive = 1)
            }

            //  스위치가 꺼지면
            else{
                Toast.makeText(this, "switch off", Toast.LENGTH_SHORT).show()
                updateFcmActive(id, fcmActive = 0)
            }
        }


    }

    private fun updateFcmActive(id: Int, fcmActive: Int) = myPageViewModel.updateFcmActive(id, fcmActive).observe(this, Observer {

    })

    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        var tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.line1Number
    }




}

