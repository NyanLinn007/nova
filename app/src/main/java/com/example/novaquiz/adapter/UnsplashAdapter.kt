package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.novaquiz.data.UnsplashPhoto
import com.example.novaquiz.databinding.ItemPhotoBinding

class UnsplashAdapter(
    private var photos: List<UnsplashPhoto> = emptyList(),
    private val onPhotoClick: (UnsplashPhoto) -> Unit
) : RecyclerView.Adapter<UnsplashAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: UnsplashPhoto) {
            Glide.with(binding.imageView.context)
                .load(photo.urls.small) // You can also use photo.urls.regular if defined
                .thumbnail(0.1f) // Show low-res preview while full loads
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // ✅ Enable caching for all sizes
                .into(binding.imageView)

            binding.imageView.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    fun submitList(newPhotos: List<UnsplashPhoto>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}

