package com.posco.posco_store.ui.main.view


import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast


import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager


import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ActivityDetailBinding
import com.posco.posco_store.ui.main.adapter.ImageAdapter


import com.posco.posco_store.ui.main.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_mypage.imageView
import kotlinx.android.synthetic.main.dialog_image_view_layout.*
import kotlinx.android.synthetic.main.item_layout.view.*


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var binding : ActivityDetailBinding
    private lateinit var detailImg : List<FileInfoDto>
    private var imageAdapter: ImageAdapter = ImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUi()

        imageAdapter.setOnItemClickListener {
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_image_view_layout)
            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        it.location + "/" + it.changedName

            Glide.with(this).load(imgUrl).placeholder(R.drawable.example_screen).error(R.drawable.example_screen).into(dialog.detail_img)

            dialog.show()


        }
    }

    // detail 화면 수정
    fun setUpUi(){


        val bundle = intent.extras
        var appDetail: App
        try {
            appDetail= bundle?.getSerializable("selected_item") as App
        }
        catch (e: Exception){
            appDetail= bundle?.get("selected_item") as App
        }
        binding.appName.text = appDetail?.appName
        binding.appIdText.text = appDetail?.id
        val fileInfo = appDetail?.iconFileInfo
        val imgUrl = "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                fileInfo?.location + "/" + fileInfo?.changedName
        val imgLocation = binding.logoImg

        detailImg = appDetail?.detailFilesInfo!!
        Log.d("details", detailImg.toString())

        imageAdapter.differ.submitList(detailImg)

        Glide.with(this).load(imgUrl).error(R.drawable.posco).override(100,100).fitCenter().into(imgLocation)

        binding.versionInfo.text = appDetail?.version
        val appInfo = appDetail.desc ?: "앱 정보가 없습니다"
        binding.appInfoText.text = appInfo
        val updateInfo = appDetail.updateDesc ?: "업데이트 정보가 없습니다"
        binding.updateInfoText.text = updateInfo
        binding.adminText.text = appDetail?.admin

        binding.appDetailBtn.setOnClickListener {
            AlertDialog.Builder(this).setTitle(binding.appInfoTextView.text).setMessage(appInfo).create().show()
        }


        binding.updateDetailBtn.setOnClickListener {

            AlertDialog.Builder(this).setTitle(binding.updateInfoTextView.text).setMessage(updateInfo).create().show()
        }


        initRecyclerView()
    }

    private fun initRecyclerView(){
        Log.d("recyclerview","start")
        binding.recyclerviewImg.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }




}

