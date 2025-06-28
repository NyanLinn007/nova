package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.novaquiz.data.UnsplashPhoto
import com.example.novaquiz.databinding.ItemPhotoBinding

class UnsplashAdapter(
    private var photos: List<UnsplashPhoto> = emptyList()
) : RecyclerView.Adapter<UnsplashAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        Glide.with(holder.itemView.context)
            .load(photo.urls.small)  // Load small image URL
            .centerCrop()
            .into(holder.binding.imageView)
    }

    // Update data and notify adapter
    fun submitList(newPhotos: List<UnsplashPhoto>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
