package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R

class MixesAdapter:RecyclerView.Adapter<MixesAdapter.MixesViewHolder>() {

    private val images= arrayOf(R.drawable.photo1
        ,R.drawable.photo2,
        R.drawable.photo3,
        R.drawable.photo4
        ,R.drawable.mixes)
    class MixesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
      var image=itemView.findViewById<ImageView>(R.id.imgmixes)
        val text: TextView = itemView.findViewById(R.id.txtmixes)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MixesAdapter.MixesViewHolder {
     var view=LayoutInflater.from(parent.context).inflate(R.layout.list_mixes,parent,false)
        return MixesViewHolder(view)
    }

    override fun onBindViewHolder(holder: MixesAdapter.MixesViewHolder, position: Int) {
        holder.image.setImageResource(images[position])

        if (position == images.size - 1) {
            holder.text.text = "Other"
            holder.itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Other", Toast.LENGTH_SHORT).show()
            }
        } else {
            holder.text.text = "Aa"
            holder.itemView.setOnClickListener(null)
        }

    }

    override fun getItemCount(): Int {
       return images.size
    }
}