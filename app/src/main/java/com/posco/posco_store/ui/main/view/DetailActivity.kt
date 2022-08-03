package com.posco.posco_store.ui.main.view


import android.os.Bundle


import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.databinding.ActivityDetailBinding

import com.posco.posco_store.ui.main.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUi()


    }

    // detail 화면 수정
    fun setUpUi(){
        val bundle = intent.extras
        val appDetail: App = bundle?.getSerializable("selected_item") as App

        binding.appName.text = appDetail?.appName
        binding.appIdText.text = appDetail?.id
        val fileInfo = appDetail?.iconFileInfoDto
        val imgUrl = "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                fileInfo?.location + "/" + fileInfo?.changedName
        val imgLocation = binding.logoImg


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
    }
}

