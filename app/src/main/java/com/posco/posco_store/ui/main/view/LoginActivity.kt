package com.posco.posco_store.ui.main.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.giahn.acDto
import com.example.giahn.giahnxois
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
import com.posco.posco_store.MainApplication
import com.posco.posco_store.R
import com.posco.posco_store.data.model.LoginDto
import com.posco.posco_store.databinding.ActivityLoginBinding
import com.posco.posco_store.ui.main.viewmodel.LoginViewModel
import com.posco.posco_store.utils.LiveSharedPreferences
import com.posco.posco_store.utils.OnSingleClickListener
import com.posco.posco_store.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var giahnxois: giahnxois


    lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val TAG = this.javaClass.simpleName

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth: FirebaseAuth

    private var email: String = ""
    private var tokenId: String? = null

    var tokened: String = ""
    private val permissions : Array<String> = arrayOf(
        Manifest.permission.READ_PHONE_NUMBERS
    )

    companion object {
        val REQUEST_CODE = 4
    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        val sharedPreference = getSharedPreferences("posco_store", Context.MODE_PRIVATE)
        val liveSharedPreferences = LiveSharedPreferences(sharedPreference)


        val editText: EditText = binding.editId
//        val editText2: EditText = binding.editPassword
        val window =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.hideSoftInputFromWindow(editText.windowToken, 0)
//        window.hideSoftInputFromWindow(editText2.windowToken, 0)

        if (!hasPermissions()) {
            Log.d("hasPermission","없어잉")
            checkAllPermission()
        }



        getFcmToken()
        setContentView(binding.root)

        try {
            giahnxois.postaccess(
                acDto(
                    "AA_015",
                    "SERVICE",
                    "로그인 화면 접속",
                    0,
                    0,
                    "A000001",
                    'A',
                    "A_015"
                )
            )
        } catch (e: java.lang.Exception) {
            Log.e("e", e.toString())
            giahnxois.posterror(
                acDto(
                    "E001",
                    "SERVICE",
                    "E_001: 통신 오류",
                    0,
                    0,
                    "A000001",
                    'A',
                    "E_015"
                )
            )
        }


        liveSharedPreferences.getString("token", "0").observe(this, Observer<String> { result ->
            if (result != "0") {
                goToMainActivity()
            }
        })

        binding.loginButton.setOnSingleClickListener {
            val ids = binding.editId.text.toString()
            val password = binding.editPassword.text.toString()
            setupAPICall(
                LoginDto(
                    userId = ids,
                    password = password,
                    type = "COMMON",
                    deviceId = getDeviceId(),
                    phoneNumber = getPhoneNumber(),
                    deviceOs = "A",
                    deviceModel = getDeviceModel(),
                    fcmToken = tokened,
                    deviceOsType = isTablet(),
                    carrier = getPhoneNetwork()
                )
            )
        }

        binding.kakaoLoginButton.setOnClickListener {

            val phonePermis =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)

            val localBuilder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.MyDialogTheme)

            if (phonePermis != PackageManager.PERMISSION_GRANTED) {
                localBuilder.setTitle("권한설정").setMessage("소셜 로그인을 위해 권한 설정이 필요합니다")
                    .setPositiveButton(
                        "권한설정하기", DialogInterface.OnClickListener { dialogInterface, i ->
                            try {
                                intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                        Uri.parse("package:com.posco.posco_store")
                                    )
                                startActivity(intent)

                            } catch (e: Exception) {
                                Log.e("e", e.toString())
                            }
                        }
                    ).setNegativeButton(
                    "취소하기", DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(this, "소셜로그인 취소", Toast.LENGTH_LONG)
                    }
                ).create().show()
            } else {
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                    UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                }

            }
        }

        firebaseAuth = FirebaseAuth.getInstance()
        Log.e("fire", firebaseAuth.toString())

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.e(TAG, "resultCode : ${result.resultCode}")
            Log.e(TAG, "result : $result")
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let { account ->
                        tokenId = account.idToken
                        if (tokenId != null && tokenId != "") {
                            val credential: AuthCredential =
                                GoogleAuthProvider.getCredential(account.idToken, null)
                            firebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (firebaseAuth.currentUser != null) {
                                        val user: FirebaseUser = firebaseAuth.currentUser!!
                                        email = user.email.toString()
                                        Log.e(TAG, "email : $email")
                                        Log.e(TAG, user.displayName.toString())

                                        val googleSignInToken = account.idToken ?: ""
                                        if (googleSignInToken != "") {
                                            Log.e(TAG, "googleSignInToken : $googleSignInToken")
                                        } else {
                                            giahnxois.posterror(
                                                acDto(
                                                    "E102",
                                                    "SERVICE",
                                                    "E_102: 로그인 토큰 없음",
                                                    0,
                                                    0,
                                                    "A000001",
                                                    'A',
                                                    "E_015"
                                                )
                                            )
                                        }

                                        setupAPICall(
                                            LoginDto(
                                                type = "SOCIAL",
                                                deviceId = getDeviceId(),
                                                phoneNumber = getPhoneNumber(),
                                                deviceOs = "A",
                                                deviceModel = getDeviceModel(),
                                                fcmToken = tokened,
                                                deviceOsType = isTablet(),
                                                carrier = getPhoneNetwork(),
                                                social = "구글"
                                            )
                                        )

                                    }
                                }
                        }
                    } ?: throw Exception()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.googleLoginButton.setOnClickListener {

            val phonePermis =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)

            val localBuilder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.MyDialogTheme)

            if (phonePermis != PackageManager.PERMISSION_GRANTED) {
                localBuilder.setTitle("권한설정").setMessage("소셜 로그인을 위해 권한 설정이 필요합니다")
                    .setPositiveButton(
                        "권한설정하기", DialogInterface.OnClickListener { dialogInterface, i ->
                            try {
                                intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                        Uri.parse("package:com.posco.posco_store")
                                    )
                                startActivity(intent);
                            } catch (e: Exception) {
                                Log.e("e", e.toString())
                            }
                        }
                    ).setNegativeButton(
                    "취소하기", DialogInterface.OnClickListener { dialogInterface, i ->
                        Toast.makeText(this, "소셜로그인 취소", Toast.LENGTH_LONG)
                    }
                ).create().show()
            } else {
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


    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true;
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode === KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder(this, R.style.MyDialogTheme).setTitle("종료").setMessage("정말 종료하시겠습니까?").setPositiveButton(
                    "네", DialogInterface.OnClickListener(){ dialogInterface, i ->
                    finish()
                }
            ).setNegativeButton("아니오",null).show();

            return true;
        }

        return super.onKeyDown(keyCode, event)
    }


    private fun setupAPICall(login: LoginDto) = loginViewModel.fetchLogin(login).observe(this) {
        when (it.status) {
            Status.SUCCESS -> {
                val id = it.data?.id
                val userId = it.data?.userId //사용자의 id

                val userName = it.data?.name
                val deviceId = it.data?.deviceId
                val token = it.data?.token

                id?.let { it1 -> MainApplication.sharedPreference.userId = id }
//                prefs.setString("userId", userId.toString())
//                prefs.setString("userName", userName.toString())
                //deviceId?.let { it1 -> prefs.setInt("deviceId", it1) }
                deviceId?.let { it1 -> MainApplication.sharedPreference.deviceId = it1 }
                userName?.let { it1 -> MainApplication.sharedPreference.userName = it1 }

                //prefs.setString("token", token.toString())
                MainApplication.sharedPreference.token = token

            }
            Status.ERROR -> {
                giahnxois.posterror(
                    acDto(
                        "E100",
                        "SERVICE",
                        "E_100: 로그인 요청 실패",
                        0,
                        0,
                        "A000001",
                        'A',
                        "E_015"
                    )
                )
                Toast.makeText(this@LoginActivity, "아이디와 비밀번호 다시 확인", Toast.LENGTH_SHORT).show()
                binding.editId.text = null
                binding.editPassword.text = null
            }
            else -> {
                Log.e("dataLength2", it.data.toString())
            }
        }

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
        } else if (token != null) {
            UserApiClient.instance.me { user, it ->
                if (it != null) {
                    Log.e("TAG", "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    setupAPICall(
                        LoginDto(
                            type = "SOCIAL",
                            deviceId = getDeviceId(),
                            phoneNumber = getPhoneNumber(),
                            deviceOs = "A",
                            deviceModel = getDeviceModel(),
                            fcmToken = tokened,
                            deviceOsType = isTablet(),
                            carrier = getPhoneNetwork(),
                            social = "카카오"

                        )
                    )
                }
            }
        }
    }

    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

    // android devcie model 확인
    fun getDeviceModel(): String {
        return Build.MODEL
    }


    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String {
        val telephony = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return telephony.line1Number.replace("+82", "0")
    }

    fun getPhoneNetwork(): String {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName
    }


    fun isTablet(): String {
        val screenSizeType =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenSizeType == Configuration.SCREENLAYOUT_SIZE_XLARGE ||
            screenSizeType == Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            return "T"
        }
        return "P"
    }


    fun getVersionInfo(): String {
        val info: PackageInfo =
            baseContext.packageManager.getPackageInfo(baseContext.packageName, 0)
        val version = info.versionName
        return version
    }

    fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
        val oneClick = OnSingleClickListener {
            onSingleClick(it)
        }
        setOnClickListener(oneClick)
    }

    fun goToMainActivity() {

        val intent = Intent(this, MainActivity::class.java)

        finishAffinity()

        startActivity(intent)
    }

    private fun getFcmToken() {
        tokened = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            tokened = task.result
            Log.d("token 확인", tokened)
            // Log and toast
            if (tokened.isBlank()) {
                val msg = "fcm 토큰 오류"
                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                giahnxois.posterror(
                    acDto(
                        "E102",
                        "SERVICE",
                        "E_102: FCM 토큰 없음",
                        0,
                        0,
                        "A000001",
                        'A',
                        "E_015"
                    )
                )

            }

        })

    }

    private fun hasPermissions(): Boolean {
        for (permission in permissions) {
            Log.d("permission 확인", permission)
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermission() {
            ActivityCompat.requestPermissions(
                this, permissions,
                REQUEST_CODE
            )

    }


    private fun checkAllPermission() {

        val localBuilder: AlertDialog.Builder = AlertDialog.Builder(this,R.style.MyDialogTheme)

        localBuilder.setTitle("포스코 서비스 이용을 위해\n접근 권한의 이용이 필요합니다.")
            .setMessage("파일(필수) \n 앱 다운로드 및 앱 설치 권한 \n\n 전화번호(필수) \n소셜로그인 및 유저 확인용 ")

            .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i -> requestPermission()  })
            .setOnDismissListener {
                requestPermission()
                it.dismiss()
            }.create().show()

    }


    override fun onPause() {
        super.onPause()

    }


}
