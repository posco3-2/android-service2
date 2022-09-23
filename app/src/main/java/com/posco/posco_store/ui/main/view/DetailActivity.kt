package com.posco.posco_store.ui.main.view


import android.app.Dialog
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.giahn.acDto
import com.example.giahn.giahnxois
import com.posco.posco_store.MainApplication
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ActivityDetailBinding
import com.posco.posco_store.ui.main.adapter.ImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_image_view_layout.*
import javax.inject.Inject


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    @Inject
    lateinit var giahnxois: giahnxois
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailImg: List<FileInfoDto>
    private var imageAdapter: ImageAdapter = ImageAdapter()
    private lateinit var downloadURL: String
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 1
    private lateinit var appDetail: App
    val userId: Int = MainApplication.sharedPreference.userId
    val deviceId: Int = MainApplication.sharedPreference.deviceId


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        setUpUi()


        try {
            giahnxois.postaccess(
                acDto(
                    "AA_017",
                    "SERVICE",
                    "디테일 페이지 접속",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "A_017"
                )
            )
        } catch (e: java.lang.Exception) {
            Log.e("e", e.toString())

            giahnxois.posterror(
                acDto(
                    "E100",
                    "HTTP 요청 실패",
                    "E_100:http request failed",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "E_017"
                )
            )
        }

        imageAdapter.setOnItemClickListener {
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_image_view_layout)
            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        it.location + "/" + it.changedName

            Glide.with(this).load(imgUrl).placeholder(R.drawable.example_screen)
                .error(R.drawable.example_screen).into(dialog.detail_img)

            dialog.show()

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("이거 맞아?", "resume")
        setUpUi()
    }

    override fun onPause() {
        super.onPause()
    }

    // detail 화면 수정
    fun setUpUi() {

        val bundle = intent.extras

        try {
            appDetail = bundle?.getSerializable("selected_item") as App
        } catch (e: Exception) {
            appDetail = bundle?.get("selected_item") as App
        }
        binding.appName.text = appDetail?.appName
        binding.appIdText.text = appDetail?.id
        val fileInfo = appDetail?.iconFileInfo
        val imgUrl =
            "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                    fileInfo?.location + "/" + fileInfo?.changedName
        val imgLocation = binding.logoImg

        try {
            //detail이 있을때
            detailImg = appDetail?.detailFilesInfo!!

            Log.d("details", detailImg.toString())

        } catch (e: java.lang.Exception) {
            giahnxois.posterror(
                acDto(
                    "AE_002",
                    "HTTP 요청 실패",
                    "E_002:http request failed",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "A_017"
                )
            )
        }
        imageAdapter.differ.submitList(detailImg)

        Glide.with(this).load(imgUrl).error(R.drawable.posco).override(100, 100).fitCenter()
            .into(imgLocation)

        binding.versionInfo.text = appDetail?.version
        val appInfo = appDetail.desc ?: "앱 정보가 없습니다"
        binding.appInfoText.text = appInfo
        val updateInfo = appDetail.updateDesc ?: "업데이트 정보가 없습니다"
        binding.updateInfoText.text = updateInfo
        binding.adminText.text = appDetail?.admin

        binding.appDetailBtn.setOnClickListener {
            AlertDialog.Builder(this, R.style.MyDialogTheme).setTitle(binding.appInfoTextView.text).setMessage(appInfo)
                .create().show()
        }

        binding.updateDetailBtn.setOnClickListener {

            AlertDialog.Builder(this, R.style.MyDialogTheme).setTitle(binding.updateInfoTextView.text)
                .setMessage(updateInfo).create().show()
        }

        val pm: PackageManager = packageManager
        var pInfo: PackageInfo? = null;
        try {
            pInfo = appDetail.packageName?.let {
                pm.getPackageInfo(
                    it,
                    PackageManager.GET_INSTRUMENTATION
                )
            }!!
            Log.d("package Info 확인", pInfo.toString())

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (pInfo != null) {
            Log.d("pInfo version", pInfo.versionName.toString() + " " + appDetail.version)
            if (pInfo.versionName.toString().equals(appDetail.version)) {
                binding.installBtn.text = "실행"
                binding.deleteBtn.isVisible = true
                binding.installBtn.setOnClickListener {
                    val intent = appDetail.packageName?.let { it1 ->
                        baseContext.packageManager.getLaunchIntentForPackage(
                            it1
                        )
                    }
                    startActivity(intent)
                }


            } else {
                binding.installBtn.text = "업데이트"
                binding.deleteBtn.isVisible = true
                installBtn()
            }


        } else {
            binding.installBtn.text = "설치"
            installBtn()
        }

        try {
            val downloadFile: FileInfoDto = appDetail?.installFileInfo!!

            downloadURL =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/download/install/" +
                        downloadFile.changedName + "?org=" + downloadFile.originalName
            Log.e("downloadUrl", downloadURL.toString())
        } catch (e: java.lang.Exception) {
            println(e)

        }
        if (appDetail?.extraUrl != null && URLUtil.isValidUrl(appDetail?.extraUrl)) {
            binding.installBtn.text = "외부 url"
            try {
                binding.installBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appDetail.extraUrl))
                    try {
                        this.startActivity(intent)
                    } catch (e: java.lang.Exception) {
                        System.out.println(e)

                    }
                }
            } catch (e: java.lang.Exception) {
                System.out.println(e)
            }

        } else if (appDetail?.extraUrl == null && appDetail?.installFileInfo == null) {
            Log.e("appDetail 확인", appDetail.installFileInfo.toString())
            binding.installBtn.text = "설치 파일이 없음"
            binding.installBtn.setTextColor(R.color.kakao_yellow)
            binding.installBtn.setOnClickListener {
                Toast.makeText(this, "설치 할 수 없습니다.", Toast.LENGTH_SHORT).show()
                giahnxois.posterror(
                    acDto(
                        "E101",
                        "설치 요청 실패 ",
                        "E_101:설치파일 없음 ",
                        deviceId,
                        userId,
                        "A000001",
                        'A',
                        "E_017"
                    )
                )
            }

        }

        binding.deleteBtn.setOnClickListener {
            Log.d("packageName", appDetail.packageName.toString())

            deleteApp(appDetail.packageName.toString())
        }


        initRecyclerView()


    }

    private fun initRecyclerView() {
        Log.d("recyclerview", "start")
        binding.recyclerviewImg.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun installBtn() {

        binding.installBtn.setOnClickListener {
            Log.d("install btn click", downloadURL)
            val intent = Intent(this@DetailActivity, DownloadActivity::class.java)

            intent.putExtra("appName", binding.appName.text)
            intent.putExtra("appInfoText", binding.appInfoText.text)
            intent.putExtra("url", downloadURL)
            intent.putExtra("scheme", appDetail.scheme)


            startActivity(intent)



        }
    }


    private fun deleteApp(packageName: String) {
        Log.d("이거 맞아", packageName)
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Intent.ACTION_DELETE).setData(packageURI)
        startActivity(intent)
        finish()


    }


}