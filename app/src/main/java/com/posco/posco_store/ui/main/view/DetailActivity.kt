package com.posco.posco_store.ui.main.view

import android.os.Bundle

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    }
}

