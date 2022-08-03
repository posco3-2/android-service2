package com.posco.posco_store.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.common.KakaoSdk.type
import com.posco.posco_store.data.model.App
import com.posco.posco_store.databinding.ActivityMainBinding
import com.posco.posco_store.ui.main.adapter.MainAdapter
import com.posco.posco_store.ui.main.viewmodel.MainViewModel
import com.posco.posco_store.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.descriptors.buildSerialDescriptor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    var adapter: MainAdapter = MainAdapter()
    private var index: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupUI()
        setupAPICall()

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("selected_item",it)
            }
            val intent = Intent(this,DetailActivity::class.java)
            intent.putExtras(bundle)

            this.startActivity(intent)
        }

        binding.settingBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            ContextCompat.startActivity(this, intent, null )
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                // 스크롤이 끝에 도달했는지 확인
                if (!binding.recyclerView.canScrollHorizontally(1) && lastVisibleItemPosition == itemTotalCount) {
                    adapter.deleteLoading()

                    Log.i("왜 안됑?", index.toString())

                    mainViewModel.getAllApp(++index)
                    setupAPICall()
                }
            }
        })
    }

    // user List View 
    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
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

        mainViewModel.getAllApp(index).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { usersData -> renderList(usersData) }
                    recyclerView.visibility = View.VISIBLE

                    adapter.notifyDataSetChanged()

                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
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
