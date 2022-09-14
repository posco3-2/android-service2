package com.posco.posco_store.ui.main.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.databinding.ItemLayoutBinding
import com.posco.posco_store.databinding.ItemLoadingBinding
import com.posco.posco_store.ui.main.view.DownloadActivity
import com.posco.posco_store.ui.main.view.MainActivity
import com.posco.posco_store.ui.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.item_layout.view.*
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MainAdapter @Inject constructor(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {


    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    var apps: ArrayList<App> = ArrayList()
    //val apps: ArrayList<App> = mainViewModel.app
    var appFilterList: ArrayList<App> = ArrayList()
    private var mShowLoading = false
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    inner class DataViewHolder(itemView: ItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val context = itemView.root.context


        fun bind(app: App) {

            itemView.textViewUserName.text = app.appName
            val fileInfo = app.iconFileInfo
            Log.i("fileinfodto", app.toString() )
            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        fileInfo?.location + "/" + fileInfo?.changedName

            Glide.with(itemView).load(imgUrl).error(R.drawable.posco)
                .into(itemView.imageViewIcon)

            itemView.textViewUserEmail.text = app.version

            Log.i("패키지명 확인",app.packageName.toString())
            val packageName = app.packageName.toString()
            val pm : PackageManager = itemView.context.packageManager

            if(isPackageInstalled(packageName, pm)){
                val pi = app.packageName?.let { pm.getPackageInfo(it, PackageManager.GET_INSTRUMENTATION) }
                if(pi != null && pi.versionName.toString().equals(app.version)){
                    Log.d("이거", packageName +" 설치됨")
                    itemView.start_btn.setImageResource(R.drawable.play)
                    itemView.start_text.text = "실행하기"
                    itemView.start_btn.setOnClickListener {
                        val intent = pm.getLaunchIntentForPackage(packageName)
                        context.startActivity(intent)
                    }
                }
               else{
                    itemView.start_btn.setImageResource(R.drawable.update)
                    itemView.start_text.text = "업데이트"

                    itemView.start_btn.setOnClickListener {
                        if(app.installFileInfo != null){
                            goToInstall(app, context)
                        }
                    }


                }
            }else{
                itemView.start_btn.setImageResource(R.drawable.download)
                itemView.start_text.text="다운로드"

                if( app.extraUrl.isNullOrBlank() || app.extraUrl.isNullOrEmpty() || !URLUtil.isValidUrl(app.extraUrl) ){
                    itemView.start_btn.setOnClickListener {
                        Log.d("확인", "dlrjrdkj")
                        if(app.installFileInfo != null){
                            goToInstall(app, context)

                        }
                        else {
                            Toast.makeText(context, "앱을 설치할 수 없습니다. ", Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{

                    Log.d("이거로",Uri.parse(app.extraUrl).toString())
                    try{
                        itemView.start_btn.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.extraUrl))
                            try {
                                context.startActivity(intent)
                            }
                            catch(e: Exception){
                                System.out.println(e)

                            }
                        }
                    }catch (e: Exception){
                        System.out.println(e)
                    }



                }
            }




            itemView.rootView.setOnClickListener {
                onItemClickListener?.let {
                    it(app)
                }
            }

        }



    }


    inner class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemViewType(position: Int): Int {
        if(position < appFilterList.size){
            return VIEW_TYPE_ITEM
        }
        return  VIEW_TYPE_LOADING

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
                return DataViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                return LoadingViewHolder(binding)
            }



        }
    }

    override fun getItemCount(): Int = appFilterList.size + if(mShowLoading) {1} else{0}

    //user data List binding
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainAdapter.DataViewHolder) {
            holder.bind(appFilterList.get(position))
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                Log.d("charString", charString)
                if (charString.isEmpty()) {
                    Log.d("apps", apps.toString())
                    appFilterList = apps
                } else {
                    val resultList = ArrayList<App>()
                    Log.d("apps확인", apps.toString())
                    for (row in apps) {
                        Log.e("appName 확인", row.appName.toString())
                        if (row.appName?.lowercase()
                                ?.contains(constraint.toString().lowercase()) == true
                        ) {
                            resultList.add(row)
                        }
                    }
                    appFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = appFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                appFilterList =
                    results?.values as ArrayList<App>
                notifyDataSetChanged()
            }
        }

    }

    fun hideLoading(){
        mShowLoading = false
        notifyItemRemoved(itemCount)
    }
    fun showLoading(){
        mShowLoading = true
        notifyItemInserted(itemCount+1)
    }

    fun addData(app: List<App>) {
        apps.addAll(app)
        appFilterList.addAll(app)
    }

    fun clear(){
        apps.clear()
        appFilterList.clear()
    }

    private var onItemClickListener:((App)->Unit)?=null

    fun setOnItemClickListener(listener: (App)->Unit){
        onItemClickListener = listener
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager) : Boolean{
        try{
            packageManager.getPackageInfo(packageName, 0)
            return true
        }catch (e : PackageManager.NameNotFoundException){
            return false
        }
    }

    private fun hasPermissions(context: Context): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun goToInstall(app: App, context: Context){
        val downloadURL = "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/download/install/" +
                app.installFileInfo?.changedName + "?org="+ app.installFileInfo?.originalName
        val intent = Intent(context, DownloadActivity::class.java)

        intent.putExtra("appName", app.appName)
        intent.putExtra("appInfoText", app.id)
        intent.putExtra("url", downloadURL)
        intent.putExtra("scheme", app.scheme)

        Log.d("이거", intent.toString())


        if(hasPermissions(context)){
            Log.i("있다","permission")
            context.startActivity(intent)
        }else{
            Log.i("없다","permission")
            val result =  requestPermission(context)
            if(result){
                context.startActivity(intent)
            }
            Toast.makeText(context, "권한을 받지 못했습니다", Toast.LENGTH_SHORT).show()
        }

    }

    private fun requestPermission(context: Context) : Boolean {
        try {
            ActivityCompat.requestPermissions(
                context as Activity, permissions,
                DownloadActivity.PERMISSION_REQUEST_CODE
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }



}



