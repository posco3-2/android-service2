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
import com.posco.posco_store.data.model.Device
import com.posco.posco_store.databinding.ActivityMypageBinding
import com.posco.posco_store.ui.main.viewmodel.MyPageViewModel
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

        try {
            giahnxois.postaccess(
                acDto(
                    "AA_018",
                    "SERVICE",
                    "세팅 페이지 접속",
                    0,
                    LoginActivity.prefs.getString("id","0" ).toInt(),
                    "A000001",
                    'A',
                    "A_018"
                )
            )
        }catch (e : Exception){
            Log.e("e",e.toString())
        }

        val userId = LoginActivity.prefs.getString("userId","No")

        binding.textView6.text  = getPhoneNumber()
        binding.textView8.text = getDeviceId()
        binding.textView12.text = userId


        val token: String = LoginActivity.prefs.getString("token","0")
        val id: Int = LoginActivity.prefs.getInt("id", 0)
        getDevice(id)
        if(token == "0"){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.imageView.setOnClickListener {
            finish()
        }

        binding.button2.setOnClickListener {
            LoginActivity.prefs.setString("token","0")
            val intent = Intent(this, LoginActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.switch1.setOnCheckedChangeListener{
        CompoundButton, onSwitch ->

            //  스위치가 켜지면
            if (onSwitch){
                updateFcmActive(id, fcmName = "fcm", Device(fcmActive = 1))
            }

            //  스위치가 꺼지면
            else{
                updateFcmActive(id, fcmName = "fcm", Device(fcmActive = 0))
            }
        }

        binding.switch2.setOnCheckedChangeListener{CompoundButton, onSwitch ->
            //  스위치가 켜지면
            if (onSwitch){
                updateFcmActive(id, fcmName = "fcmWorking", Device(fcmActive = 1))
            }
            //  스위치가 꺼지면
            else{
                updateFcmActive(id, fcmName = "fcmWorking", Device(fcmActive = 0))
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

