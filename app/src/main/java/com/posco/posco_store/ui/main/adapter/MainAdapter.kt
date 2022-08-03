package com.posco.posco_store.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ItemLayoutBinding
import com.posco.posco_store.databinding.ItemLoadingBinding
import com.posco.posco_store.ui.main.view.DetailActivity

import kotlinx.android.synthetic.main.item_layout.view.*
import kotlinx.coroutines.NonDisposableHandle.parent

import javax.inject.Inject
import kotlin.collections.ArrayList

class MainAdapter @Inject constructor(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM =0
    private val VIEW_TYPE_LOADING =1
    private var apps: ArrayList<App> = ArrayList()
    private var appFilterList: ArrayList<App> = ArrayList()
    private var files: ArrayList<FileInfoDto> = ArrayList()


    inner class DataViewHolder(itemView: ItemLayoutBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(app: App) {
            itemView.textViewUserName.text = app.appName
            val fileInfo: FileInfoDto = app.iconFileInfoDto!!
            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        fileInfo.location + "/" + fileInfo.changedName

            Glide.with(itemView).load(imgUrl).error(R.drawable.poscologo)
                .into(itemView.imageViewIcon)

            itemView.textViewUserEmail.text = app.version

        }

    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemViewType(position: Int): Int {
        return when(appFilterList[position].appName){
            " " -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
                DataViewHolder(binding)
            }
            else ->{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = appFilterList.size

    //user data List binding
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is DataViewHolder){
            holder.bind(appFilterList.get(position))
        }else{

        }
        //holder.bind(appFilterList.get(position))

//        //클릭시 user detail page로 이동
//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView?.context, DetailActivity::class.java)
//            // intent로 id값 전달
//            intent.putExtra("id", "2")
//            ContextCompat.startActivity(holder.itemView.context, intent, null)
//        }
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    appFilterList = apps
                } else {
                    val resultList = ArrayList<App>()
                    for (row in apps) {
                        if (row.appName?.toLowerCase()
                                ?.contains(constraint.toString().toLowerCase()) == true
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
                    results?.values as ArrayList<App> /* = java.util.ArrayList<com.posco.posco_store.data.model.App> */
                notifyDataSetChanged()
            }
        }

    }


    fun deleteLoading(){
        apps.removeAt(apps.lastIndex)
    }


    fun addData(app: List<App>) {
        this.apps.apply {
            clear()
            addAll(app)

        }
        apps.add(App(" ", " "))

        this.appFilterList.apply {
            clear()
            addAll(app)
        }
        appFilterList.add(App(" ", " "))



    }




}



