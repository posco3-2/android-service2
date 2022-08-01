package com.rajkumarrajan.mvvm_architecture.ui.main.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.App
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.ui.main.view.DetailActivity
import kotlinx.android.synthetic.main.item_layout.view.*
import javax.inject.Inject

class MainAdapter @Inject constructor(
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    private var apps: ArrayList<App> = ArrayList()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(app: App) {
            itemView.textViewUserName.text = app.appName
            itemView.textViewUserEmail.text = app.version

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = apps.size

    //user data List binding
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(apps[position])

        //클릭시 user detail page로 이동
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView?.context, DetailActivity::class.java)
            // intent로 id값 전달
            intent.putExtra("id", position+1)
            ContextCompat.startActivity(holder.itemView.context, intent, null )
        }
    }


    fun addData(app: List<App>) {
        this.apps.apply {
            clear()
            addAll(app)
        }
    }

}