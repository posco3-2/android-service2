package com.rajkumarrajan.mvvm_architecture.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakao.sdk.user.UserApiClient
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.App
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.databinding.ActivityMainBinding
import com.rajkumarrajan.mvvm_architecture.ui.main.adapter.MainAdapter
import com.rajkumarrajan.mvvm_architecture.ui.main.viewmodel.MainViewModel
import com.rajkumarrajan.mvvm_architecture.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding


    @Inject
    lateinit var adapter: MainAdapter


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupUI()
        setupAPICall()

        binding.button.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            ContextCompat.startActivity(this, intent, null )
        }

    }

    // user List View 
    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter()
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }


    // User Data overserver
    private fun setupAPICall() {

        mainViewModel.getAllApp().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { usersData -> renderList(usersData) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // adapter에 user data list data 추가
    private fun renderList(apps: List<App>) {
        adapter.apply {
            addData(apps)
            notifyDataSetChanged()
        }
    }
}
