package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.databinding.ActivityDetailBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.DetailViewModel
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    //DetailView Model observe
    private fun setupAPIcall(testInt: Int){
        detailViewModel.fetchUserId(testInt).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }

        })

    }

}

