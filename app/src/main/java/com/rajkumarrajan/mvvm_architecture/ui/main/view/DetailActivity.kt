package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.os.Bundle
import android.view.View
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.DetailViewModel
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 넘겨준 Id값 받아서 detail data call
        val getid = getIntent().getIntExtra("id", 1)
        setupAPIcall(getid)
    }

    //DetailView Model observe
    private fun setupAPIcall(testInt: Int){
        detailViewModel.fetchUserId(testInt).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    textView5.text = it.data.toString()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

}

