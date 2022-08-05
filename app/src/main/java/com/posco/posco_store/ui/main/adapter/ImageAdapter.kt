package com.posco.posco_store.ui.main.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.posco.posco_store.R
import com.posco.posco_store.data.model.App
import com.posco.posco_store.data.model.FileInfoDto
import com.posco.posco_store.databinding.ItemImageListBinding
import kotlinx.android.synthetic.main.item_image_list.view.*

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<FileInfoDto>(){
        override fun areItemsTheSame(oldItem: FileInfoDto, newItem: FileInfoDto): Boolean {
            return oldItem.changedName == newItem.changedName
        }

        override fun areContentsTheSame(oldItem: FileInfoDto, newItem: FileInfoDto): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,callback)

    inner class ImageViewHolder(val binding : ItemImageListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(fileInfoDto: FileInfoDto){

            val imgUrl =
                "http://ec2-43-200-14-78.ap-northeast-2.compute.amazonaws.com:8000/file-service/file/image/" +
                        fileInfoDto?.location + "/" + fileInfoDto?.changedName

            Glide.with(itemView).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.example_screen).error(R.drawable.example_screen)).load(imgUrl)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                }).into(itemView.imageView_item)

            itemView.rootView.setOnClickListener {
                onItemClickListener?.let {
                    it(fileInfoDto)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = differ.currentList[position]
        Log.i("images",image.toString())
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener:((FileInfoDto)->Unit)?=null

    fun setOnItemClickListener(listener: (FileInfoDto)->Unit){
        onItemClickListener = listener
    }

}