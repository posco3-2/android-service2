package com.posco.posco_store.ui.main.view


import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ActivityDetailBinding
import com.posco.posco_store.ui.main.adapter.ImageAdapter
import com.posco.posco_store.ui.main.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.dialog_image_view_layout.*

import java.io.File
import java.net.URL


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailImg: List<FileInfoDto>
    private var imageAdapter: ImageAdapter = ImageAdapter()
    private lateinit var downloadURL: String
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.posco.posco_store.databinding.ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUi()

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
        val completeFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadCompleteReceiver, completeFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(downloadCompleteReceiver)
    }

    // detail 화면 수정
    fun setUpUi() {

        val bundle = intent.extras
        var appDetail: App
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

        detailImg = appDetail?.detailFilesInfo!!
        Log.d("details", detailImg.toString())

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
            AlertDialog.Builder(this).setTitle(binding.appInfoTextView.text).setMessage(appInfo)
                .create().show()
        }


        binding.updateDetailBtn.setOnClickListener {

            AlertDialog.Builder(this).setTitle(binding.updateInfoTextView.text)
                .setMessage(updateInfo).create().show()
        }

        val downloadFile: FileInfoDto = appDetail?.installFileInfo!!

        downloadURL =
            "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/download/install/" +
                    downloadFile.changedName + "?org="+ downloadFile.originalName




        initRecyclerView()
        installBtn()

    }

    private fun initRecyclerView() {
        Log.d("recyclerview", "start")
        binding.recyclerviewImg.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun installBtn() {
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        binding.installBtn.setOnClickListener {
            Log.d("install btn click",downloadURL)
            //  supportFragmentManager.beginTransaction().replace(R.id.)
            //URLDownloading(Uri.parse(downloadURL))
//            val intent = Intent(this@DetailActivity, DownloadProgressFragment::class.java)
//            intent.putExtra("appName", binding.appName.text)
//            intent.putExtra("appInfoText", binding.appInfoText.text)
            // intent.putExtra("url", downloadURL)
        }
    }

    private fun URLDownloading(url: Uri) {
        val sdCard = Environment.getExternalStorageDirectory()
        // val outputFile: File = File(sdCard.absoluteFile, "/poscoStore")
        val outputFilePath : String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/poscoStore").toString().plus("/${binding.appName.text}.apk")
        val outputFile = File(outputFilePath)
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile?.mkdirs()
        }

        val request = DownloadManager.Request(url)

        request.setTitle(binding.appName.text)
        Log.d("이거머양", binding.appInfoText.text.toString())
        request.setDescription(binding.appInfoText.text)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationUri(Uri.fromFile(outputFile))
        request.setAllowedOverMetered(true)
        downloadID = downloadManager.enqueue(request)


    }

    private val downloadCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == reference) {
                val query = DownloadManager.Query() // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference)
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val columnReason: Int = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val status: Int = cursor.getInt(columnIndex)
                val reason: Int = cursor.getInt(columnReason)
                cursor.close()
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> Toast.makeText(
                        this@DetailActivity,
                        "다운로드를 완료하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    DownloadManager.STATUS_PAUSED -> Toast.makeText(
                        this@DetailActivity,
                        "다운로드가 중단되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    DownloadManager.STATUS_FAILED -> Toast.makeText(
                        this@DetailActivity,
                        "다운로드가 취소되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteDownloadFile(){
        downloadManager.remove(downloadID)
    }



}
