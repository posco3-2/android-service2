package com.posco.posco_store.ui.main.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giahn.acDto
import com.example.giahn.giahnxois
import com.google.firebase.messaging.FirebaseMessaging
import com.posco.posco_store.MainApplication
import com.posco.posco_store.data.model.App
import com.posco.posco_store.databinding.ActivityMainBinding
import com.posco.posco_store.ui.main.adapter.MainAdapter
import com.posco.posco_store.ui.main.viewmodel.MainViewModel
import com.posco.posco_store.utils.LiveSharedPreferences
import com.posco.posco_store.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var giahnxois: giahnxois

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: MainAdapter
    private var token: String? = null
    private var index: Int = 0
    private var userId: Int = 0
    private var deviceId: Int = 0

    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MainAdapter()

        setupUI()

        userId = MainApplication.sharedPreference.userId //LoginActivity.prefs.getInt("id",0 )
        token = MainApplication.sharedPreference.token //LoginActivity.prefs.getString("token","0" )
        deviceId = MainApplication.sharedPreference.deviceId

        var result = false

        mainViewModel.getUserIdCheck(userId,getDeviceId()).observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data == false){
                        MainApplication.sharedPreference.token = "0"
                        val intent = Intent(this, LoginActivity::class.java)
                        finishAffinity()
                        startActivity(intent)
                    }

                }
                Status.ERROR -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
                else -> {}
            }
        }


        if (token == "0") {
            val intent = Intent(this, LoginActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        try {
            giahnxois.postaccess(
                acDto(
                    "AA_016",
                    "SERVICE",
                    "리스트 페이지 접속",
                    deviceId,
                    userId,
                    "A000001",
                    'A',
                    "A_016"
                )
            )
        } catch (e: java.lang.NullPointerException) {
            Log.e("e", e.toString())
        }

        Log.d("이고", deviceId.toString())

        mainViewModel.getFcm(deviceId).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.fcmActive?.let { it1 ->
                        MainApplication.sharedPreference.tokenActive = it1
                    }//LoginActivity.prefs.setInt("fcmActive" , it1) }
                    it.data?.updateFcmActive?.let { it1 ->
                        MainApplication.sharedPreference.updateTokenActive = it1
                    }
                    Log.d("it.data.fcm", it.data.toString())
                }
                Status.ERROR -> {
                    Log.d("it.data 실패", it.message.toString())
                    MainApplication.sharedPreference.tokenActive = 1
                    MainApplication.sharedPreference.updateTokenActive = 1
                }
                else -> {}
            }
        }

        val sharedPreference =
            getSharedPreferences("posco_store", Context.MODE_PRIVATE)
        val liveSharedPreference = LiveSharedPreferences(sharedPreference)


        liveSharedPreference.getInt("tokenActive", 0).observe(this) { result ->
            Log.d("왱", result.toString())
            if (result.equals(1)) {
                FirebaseMessaging.getInstance().subscribeToTopic("A000001")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("A000001", "구독 요청 성공")
                        } else {
                            Log.i("A000001", "구독 요청 실패")
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("A000001")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("A000001", "구독 취소 요청 성공")
                        } else {
                            Log.i("A000001", "구독 취소 요청 실패")
                        }
                    }
            }
        }




        setupAPICall()


        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("selected_item", it)
            }
            Log.i("bundle 확인", bundle.getSerializable("selected_item").toString())
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtras(bundle)
            this.startActivity(intent)
        }



        binding.settingBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.refreshBtn.setOnClickListener {
            binding.searchEdit.setText(null)
            index = 0
            adapter.clear()
            mainViewModel.app.clear()
            setupAPICall()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "새로고침 했습니다", Toast.LENGTH_SHORT).show()

        }

        binding.searchEdit.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                handled = true
            }
            handled
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                val visibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.childCount
                val itemTotalCount = recyclerView.adapter!!.itemCount  // 어댑터에 등록된 아이템의 총 개수 -1
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition() //첫번째 보이는것

                // 스크롤이 끝에 도달했는지 확인
                // if ( lastVisibleItemPosition == itemTotalCount) {
                if (!isLoading) {
                    adapter.showLoading()
                    if ((visibleItemCount + firstVisibleItemPosition) >= itemTotalCount) {
                        index += 10
                        mainViewModel.getAppListByUser(userId, index)
                        //mainViewModel.getAllApp(index)
                        adapter.notifyDataSetChanged()
                        setupAPICall()
                    }
                }
            }
        })

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true;
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode === KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this).setTitle("종료").setMessage("정말 종료하시겠습니까?").setPositiveButton(
                "네"
            ) { dialogInterface, i ->
                finish()
            }.setNegativeButton("아니오", null).show();

            return true;
        }

        return super.onKeyDown(keyCode, event)
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

        mainViewModel.getAppListByUser(userId, index).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    adapter.clear()
                    mainViewModel.app.clear()
                    isLoading = true
                    progressBar.visibility = View.GONE


                    it.data?.let { it -> mainViewModel.app.addAll(it) }

                    //it.data?.let { usersData -> renderList(usersData) }
                    renderList(mainViewModel.app)
                    recyclerView.visibility = View.VISIBLE

                    adapter.notifyDataSetChanged()
                    checkUri()
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

                    giahnxois.posterror(
                        acDto(
                            "E100",
                            "SERVICE",
                            "E_100:" + it.message,
                            deviceId,
                            userId,
                            "A000001",
                            'A',
                            "E_016"
                        )
                    )
                }
                else -> {}
            }
        }
    }

    // adapter에 user data list data 추가
    private fun renderList(apps: List<App>) {
        adapter.apply {
            addData(apps)
            notifyDataSetChanged()
        }
    }


    fun checkUri() {
        val action: String? = intent.action
        val data: Uri? = intent.data

        if (action == Intent.ACTION_VIEW) {
            val page = data?.getQueryParameter("page")
            val appIds = data?.getQueryParameter("appId")
            Log.d("page", page.toString())
            Log.d("appId", appIds.toString())
            Log.e("있나", mainViewModel.app.toString())

            val oneApp = mainViewModel.app.filter { it ->
                it.id == appIds
            }
            if (!oneApp.isEmpty()) {
                val bundle = Bundle().apply {
                    putSerializable("selected_item", oneApp.get(0))
                }
                Log.e("확인!!!", oneApp.get(0).toString())
                try {
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtras(bundle)
                    this.startActivity(intent)
                } catch (e: Exception) {
                    giahnxois.posterror(
                        acDto(
                            "E103",
                            "SERVICE",
                            "E_103:" + e.message,
                            deviceId,
                            userId,
                            "A000001",
                            'A',
                            "E_016"
                        )
                    )
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    fun getDeviceId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

}