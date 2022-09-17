package com.posco.posco_store.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.giahn.acDto
import com.example.giahn.giahnxois
import com.posco.posco_store.MainApplication
import com.posco.posco_store.data.model.Device
import com.posco.posco_store.databinding.ActivityMypageBinding
import com.posco.posco_store.ui.main.viewmodel.MyPageViewModel
import com.posco.posco_store.utils.LiveSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {
    @Inject
    lateinit var giahnxois: giahnxois

    private val myPageViewModel: MyPageViewModel by viewModels()
    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = MainApplication.sharedPreference.userId
        val deviceId =MainApplication.sharedPreference.deviceId

        val sharedPreference = getSharedPreferences("posco_store", Context.MODE_PRIVATE)
        val liveSharedPreferences = LiveSharedPreferences(sharedPreference)

        try {
            giahnxois.postaccess(
                acDto(
                    "AA_018",
                    "SERVICE",
                    "세팅 페이지 접속",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "A_018"
                )
            )
        }catch (e : Exception){
            giahnxois.posterror(
                acDto(
                    "E001",
                    "SERVICE",
                    "E_001: 세팅 페이지 접속 실패",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "E_018"
                )
            )
        }


        binding.textView6.text  = getPhoneNumber()
        binding.textView8.text = getDeviceId()
        binding.textView12.text = userId.toString()


        liveSharedPreferences.getString("token", "0").observe(this, Observer {
            result ->
            if(result == "0"){
                val intent = Intent(this, LoginActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        })




        getDevice(userId)


        binding.imageView.setOnClickListener {
            finish()
        }

        binding.button2.setOnClickListener {
            MainApplication.sharedPreference.token = "0"
//            //LoginActivity.prefs.setString("token","0")
//            val intent = Intent(this, LoginActivity::class.java)
//            finishAffinity()
//            startActivity(intent)
        }

        binding.switch1.setOnCheckedChangeListener{
        CompoundButton, onSwitch ->

            //  스위치가 켜지면
            if (onSwitch){
                updateFcmActive(userId, fcmName = "fcm", Device(fcmActive = 1))
                MainApplication.sharedPreference.tokenActive = 1
                //LoginActivity.prefs.setInt("fcmActive", 1)
            }

            //  스위치가 꺼지면
            else{
                updateFcmActive(userId, fcmName = "fcm", Device(fcmActive = 0))
                MainApplication.sharedPreference.tokenActive = 0
                //LoginActivity.prefs.setInt("fcmActive", 0)
            }
        }

        binding.switch2.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            //  스위치가 켜지면
            if (onSwitch){
                updateFcmActive(userId, fcmName = "fcmWorking", Device(fcmActive = 1))
                MainApplication.sharedPreference.updateTokenActive = 1
                //LoginActivity.prefs.setInt("updateFcmActive", 1)
            }
            //  스위치가 꺼지면
            else{
                updateFcmActive(userId, fcmName = "fcmWorking", Device(fcmActive = 0))
                MainApplication.sharedPreference.updateTokenActive = 0
                //LoginActivity.prefs.setInt("updateFcmActive", 0)
            }
        }


    }

    private fun updateFcmActive(id: Int, fcmName:String, device: Device) = myPageViewModel.updateFcmActive(id, fcmName, device).observe(this, Observer {


    })

    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return "0" + tm.line1Number.substring(3 )
    }

    private fun getDevice(userId: Int) = myPageViewModel.getDeivce(userId).observe(this, Observer {

        if(it.data?.fcmActive == 1) {
            binding.switch1.isChecked = true
        }

        if(it.data?.updateFcmActive == 1 ){
            binding.switch2.isChecked = true
        }

    })



}

