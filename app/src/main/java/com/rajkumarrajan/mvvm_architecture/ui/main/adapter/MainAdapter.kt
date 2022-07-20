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
import com.rajkumarrajan.mvvm_architecture.data.model.User
import com.rajkumarrajan.mvvm_architecture.ui.main.view.DetailActivity
import kotlinx.android.synthetic.main.item_layout.view.*
import javax.inject.Inject

class MainAdapter @Inject constructor(
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    private var users: ArrayList<User> = ArrayList()


    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            itemView.textViewUserName.text = user.name
            itemView.textViewUserEmail.text = user.user_id

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        Log.i("MainActivity", position.toString())
        holder.bind(users[position])
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView?.context, DetailActivity::class.java)
            intent.putExtra("id", position+1)
            ContextCompat.startActivity(holder.itemView.context, intent, null )
        }
    }


    fun addData(users: List<User>) {
        this.users.apply {
            clear()
            addAll(users)
        }
    }

}