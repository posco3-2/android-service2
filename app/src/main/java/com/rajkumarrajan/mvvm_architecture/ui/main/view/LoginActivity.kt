package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User

import com.rajkumarrajan.mvvm_architecture.databinding.ActivityLoginBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.LoginViewModel
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val id = binding.id.text.toString()
            val password = binding.password.text.toString()

            setupAPICall(User(user_id = id, password = password))



        }

    }

    private fun setupAPICall(user: User) = loginViewModel.fetchLogin(user).observe(this, Observer {
        when (it.status) {
            Status.SUCCESS -> {
                if( it.data?.size == 0){

                    Toast.makeText(this@LoginActivity,"아이디와 비밀번호 다시 확인", Toast.LENGTH_SHORT).show()
                    binding.id.text = null;
                    binding.password.text = null;
                }
                if(it.data?.get(0)?.id.toString() != null){
                   inputDevice(Device(
                       device_id = getDeviceId(),
                       phone_number = "dlspds",
                       user_id = it.data?.get(0)?.id,
                       user_name = it.data?.get(0)?.name,
                       device_os = 'A',
                       device_model = getDeviceModel(),
                   ))
                }
            }
            Status.ERROR -> {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

    })

    private fun inputDevice(device: Device) = loginViewModel.inputDevice(device).observe(this, Observer {
        when (it.status){
            Status.SUCCESS ->{
                val intent = Intent(this, MainActivity::class.java)
                ContextCompat.startActivity(this, intent, null )
            }
            Status.ERROR -> {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

    })


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
