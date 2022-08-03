package com.posco.posco_store.ui.main.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.Filter


import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ItemLayoutBinding
import com.posco.posco_store.databinding.ItemLoadingBinding
import com.posco.posco_store.ui.main.view.DetailActivity

import kotlinx.android.synthetic.main.item_layout.view.*

import javax.inject.Inject
import kotlin.collections.ArrayList

class MainAdapter @Inject constructor(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var apps: ArrayList<App> = ArrayList()
    var appFilterList: ArrayList<App> = ArrayList()
    private var files: ArrayList<FileInfoDto> = ArrayList()


    inner class DataViewHolder(itemView: ItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        fun bind(app: App) {
            itemView.textViewUserName.text = app.appName
            val fileInfo = app.iconFileInfoDto
            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        fileInfo?.location + "/" + fileInfo?.changedName

            Glide.with(itemView).load(imgUrl).error(R.drawable.posco)
                .into(itemView.imageViewIcon)

            itemView.textViewUserEmail.text = app.version

            itemView.rootView.setOnClickListener {
                onItemClickListener?.let {
                    it(app)
                }
            }

        }

    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemViewType(position: Int): Int {
        return when (appFilterList[position].appName) {
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
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = appFilterList.size

    //user data List binding
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            holder.bind(appFilterList.get(position))
        }
        //holder.bind(appFilterList.get(position))

        //클릭시 user detail page로 이동
//        holder.itemView.setOnClickListener {
//            val bundle = Bundle().apply {
//                putSerializable("selected_item", appFilterList[position])
//            }
//            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
//            Log.d("이거 왜 안됑", "??")
//            startActivity(holder.itemView.context, intent, bundle)
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
            }
        }

    }


    fun deleteLoading() {
        apps.removeAt(apps.lastIndex)
        appFilterList.removeAt(appFilterList.lastIndex)
    }


    fun addData(app: List<App>) {
        apps.addAll(app)
        appFilterList.addAll(app)
        apps.add(App(" ", " "))
        appFilterList.add(App(" ", " "))


    }

    private var onItemClickListener:((App)->Unit)?=null

    fun setOnItemClickListener(listener: (App)->Unit){
        onItemClickListener = listener
    }


}



