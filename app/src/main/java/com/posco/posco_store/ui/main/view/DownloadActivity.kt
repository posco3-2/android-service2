package com.posco.posco_store.ui.main.view

import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.giahn.acDto
import com.example.giahn.giahnxois
import com.posco.posco_store.MainApplication
import com.posco.posco_store.databinding.ActivityDownloadBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class DownloadActivity : AppCompatActivity(), OnFileDownloadingCallback {
    @Inject
    lateinit var giahnxois: giahnxois
    private lateinit var mBinding: ActivityDownloadBinding
    private lateinit var mAppName: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        try {
            giahnxois.postaccess(
                acDto(
                    "AA_019",
                    "SERVICE",
                    "다운로드 페이지 접속",
                    MainApplication.sharedPreference.deviceId,
                    MainApplication.sharedPreference.userId, // LoginActivity.prefs.getString("id","0" ).toInt(),
                    "A000001",
                    'A',
                    "A_019"
                )
            )
        }catch (e : java.lang.Exception){
            Log.e("e",e.toString())
            giahnxois.posterror(
                acDto(
                    "E001",
                    "SERVICE",
                    "E_001: download page error",
                    MainApplication.sharedPreference.deviceId,
                    MainApplication.sharedPreference.userId, // LoginActivity.prefs.getString("id","0" ).toInt(),
                    "A000001",
                    'A',
                    "E_019"
                )
            )
        }

        setUpUi()

        mBinding.btnUpdateApp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }




    /**
     * author : 김지현
     * param : x
     * return :반환값
     * see :함수명
     * since : 2022-08-18
     * comment : intent에 있는것 가져오기
     *
     **/

    fun setUpUi(){
        val appName = intent.getStringExtra("appName")
        val url = intent.getStringExtra("url")
        val appInfoText = intent.getStringExtra("appInfoText")
        val scheme = intent.getStringExtra("scheme")
        mAppName = scheme!!.replace("//", "")
        Log.i("appName, url, appinfotext, scheme", appName + " " +url +" "+ appInfoText + " "+ mAppName)
        createOutputFile()
        downloadFile(this@DownloadActivity)
    }


    /**
     * author :
     * param : 매개변수
     * return :반환값
     * see :함수명
     * since : 2022-08-18
     * comment : apkfile 만들기
     *
     **/

    var apkFile: File? = null
    var isDownloadSuccess = false

    private fun createOutputFile() {
        Log.i("이거 먼데", "$mAppName.apk")
        apkFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$mAppName.apk")
        if (apkFile?.exists() == true) {
            apkFile?.delete()
        }
    }

    /**
     * author : 김지현
     * param : callback 메소드
     * return :반환값
     * see :downloadFile
     * since : 2022-08-18
     * comment : 다운로드하기
     *
     **/
    private fun downloadFile(
        callback: OnFileDownloadingCallback
    ) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            handler.post { callback.onDownloadingStart() }
            try {
                val url = URL(intent?.getStringExtra("url").toString())
                val conection = url.openConnection()
                conection.connect()
                val lenghtOfFile = conection.contentLength
                val input: InputStream = BufferedInputStream(url.openStream(), 8192)
                val output = FileOutputStream(apkFile)
                val data = ByteArray(1024)
                var total: Long = 0
                while (true) {
                    val read = input.read(data)
                    if (read == -1) {
                        break
                    }
                    output.write(data, 0, read)
                    val j2 = total + read.toLong()
                    if (lenghtOfFile > 0) {
                        val progress = ((100 * j2 / lenghtOfFile.toLong()).toInt())
                        callback.onDownloadingProgress(progress)
                    }
                    total = j2
                }
                output.flush()
                output.close()
                input.close()
                isDownloadSuccess = true
                handler.post { callback.onDownloadingComplete() }
            } catch (e: Exception) {
                isDownloadSuccess = false
                handler.post { callback.onDownloadingFailed(e) }
            }
        }
    }


    private fun installApk() {
        Log.d("downloadFile", " canRequestPackageInstalls ")
        Log.d("downloadFile", " installApk ")
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri = FileProvider.getUriForFile(this, "$packageName.provider", apkFile!!)
            Log.i("이거 머지", "$packageName.provider")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            intent.data = apkUri
        } else {
            intent.setDataAndType(
                Uri.fromFile(apkFile),
                "application/vnd.android.package-archive"
            )
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        Log.d("downloadFile", " installed ")


    }


    override fun onDownloadingStart() {
        runOnUiThread {
            Log.d("downloadFile", " onDownloadingStart ")
            mBinding.tvInfo.text = "Downloading"
            mBinding.tvInfo.visibility = View.VISIBLE
            mBinding.pbHorizontal.visibility = View.VISIBLE
            mBinding.pbHorizontal.progress = 0
            mBinding.btnUpdateApp.visibility = View.GONE
        }
    }

    override fun onDownloadingProgress(progress: Int) {
        runOnUiThread {
            Log.d("downloadFile", " onDownloadingProgress " + progress)
            mBinding.pbHorizontal.progress = progress
            mBinding.tvInfo.text = "$progress%"
        }
    }

    override fun onDownloadingComplete() {
        runOnUiThread {
            Log.d("downloadFile", " onDownloadingComplete ")
            mBinding.btnUpdateApp.visibility = View.VISIBLE
            mBinding.pbHorizontal.visibility = View.VISIBLE
            mBinding.tvInfo.text = "Completed"
            installApk()
        }
    }

    override fun onDownloadingFailed(e: Exception?) {
        runOnUiThread {
            Log.d("downloadFile", " onDownloadingFailed " + e?.message)
            mBinding.btnUpdateApp.visibility = View.VISIBLE
            mBinding.pbHorizontal.visibility = View.GONE
            mBinding.tvInfo.visibility = View.GONE
            mBinding.tvInfo.text = "Downloading Failed"

            giahnxois.posterror(
                acDto(
                    "E104",
                    "SERVICE",
                    "E_001: downloading error" + e.toString(),
                    MainApplication.sharedPreference.deviceId,
                    MainApplication.sharedPreference.userId, // LoginActivity.prefs.getString("id","0" ).toInt(),
                    "A000001",
                    'A',
                    "E_019"
                )
            )
        }
    }


}

internal interface OnFileDownloadingCallback {
    fun onDownloadingStart()
    fun onDownloadingProgress(progress: Int)
    fun onDownloadingComplete()
    fun onDownloadingFailed(e: Exception?)
}