package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.Device
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.databinding.ActivityLoginBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.LoginViewModel
import com.rajkumarrajan.mvvm_architecture.utils.MySharedPreferences
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    companion object {
        lateinit var prefs : MySharedPreferences
    }

    lateinit var binding : ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val TAG = this.javaClass.simpleName

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    private var email: String = ""
    private var tokenId: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)




        setContentView(binding.root)

        binding.button.setOnClickListener {
            val id = binding.id.text.toString()
            val password = binding.password.text.toString()

            setupAPICall(User(userId = id, password = password))

        }
        binding.kakaoLoginButton.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()
        Log.e("fire",firebaseAuth.toString())

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                Log.e(TAG, "resultCode : ${result.resultCode}")
                Log.e(TAG, "result : $result")
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        task.getResult(ApiException::class.java)?.let { account ->
                            tokenId = account.idToken
                            if (tokenId != null && tokenId != "") {
                                val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                                firebaseAuth.signInWithCredential(credential)
                                    .addOnCompleteListener {
                                        if (firebaseAuth.currentUser != null) {
                                            val user: FirebaseUser = firebaseAuth.currentUser!!
                                            email = user.email.toString()
                                            Log.e(TAG, "email : $email")
                                            Log.e(TAG,  user.displayName.toString())
                                            kakaocheck(User(name = user.displayName.toString()
                                                ,phoneNumber=getPhoneNumber()))
                                            val googleSignInToken = account.idToken ?: ""
                                            if (googleSignInToken != "") {
                                                Log.e(TAG, "googleSignInToken : $googleSignInToken")
                                            } else {
                                                Log.e(TAG, "googleSignInToken이 null")
                                            }
                                        }
                                    }
                            }
                        } ?: throw Exception()
                    }   catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })

            binding.run {
                googleLoginButton.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
                        val signInIntent: Intent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                }
            }


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                    22
                )
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

                    val userId = it.data?.get(0)?.id
                    val userName = it.data?.get(0)?.name

                    it.data?.get(0)?.id?.toInt()?.let { it1 -> checkRegiDevice(it1,
                        userId!!, userName!!) }


                }
            }
            Status.ERROR -> {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

    })

    private fun kakaocheck(user: User) = loginViewModel.checkKakao(user).observe(this, Observer {
        when (it.status){
            Status.SUCCESS ->{
                if(it.data?.get(0)?.id.toString() != null){
                    val userId = it.data?.get(0)?.id
                    val userName = it.data?.get(0)?.name
                    it.data?.get(0)?.id?.toInt()?.let { it1 -> checkRegiDevice(it1,
                        userId!!, userName!!) }
                }

            }
            Status.ERROR -> {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }

    })


    private fun checkRegiDevice(id: Int, userId: Int, userName: String) = loginViewModel.checkRegiDevice(id).observe(this, Observer {
        when (it.status){


            Status.SUCCESS ->{

                Log.e("regi", (it.data.toString() == "1").toString());
                if(it.data.toString() == "1"){
                    prefs.setString("id", id.toString())
                    prefs.setString("userName", binding.id.text.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    ContextCompat.startActivity(this, intent, null )

                }else{
                    Log.e("처음등록하는","아이디")
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        val token = task.result
                        prefs.setString("userName", binding.id.text.toString())
                        prefs.setString("id", id.toString())

                        inputDevice(Device(
                            deviceId = getDeviceId(),
                            phoneNumber = getPhoneNumber(),
                            userId = userId,
                            userName = userName,
                            deviceOs = 'A',
                            deviceModel = getDeviceModel(),
                            regDate = LocalDateTime.now().toString(),
                            updateDate = LocalDateTime.now().toString(),
                            fcmToken = token ,
                            deviceOsType = isTablet(),
                            carrier = getPhoneNetwork()
                        ))

                    })
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

    private fun updateDevice(id: Int, updateDate: String) = loginViewModel.updateRegiDevice(id, updateDate).observe(this, Observer {
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

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                }
                else -> { // Unknown
                    Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (token != null) {
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("TAG", "사용자 정보 요청 실패", error)
                }
                else if (user != null) {
                    Log.e("TAG", user.kakaoAccount?.profile?.nickname.toString())
                    Log.e("TAG", getPhoneNumber())
                    kakaocheck(User(name = user.kakaoAccount?.profile?.nickname.toString()
                                    ,phoneNumber=getPhoneNumber()))
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        var tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.line1Number
    }

    fun getPhoneNetwork(): String {
        var tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName
    }


    fun isTablet(): Char {
        val screenSizeType = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenSizeType == Configuration.SCREENLAYOUT_SIZE_XLARGE ||
            screenSizeType == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return 'T'
        }
        return 'P'
    }

}
