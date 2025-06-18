package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R

class GeneralForyouAdapter:RecyclerView.Adapter<GeneralForyouAdapter.ForyouViewHolder>() {

    private val items= listOf(
        "ForYou 1",
        "ForYou 2",
        "ForYou 3"
    )
    class ForyouViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
      val txtforyou=itemView.findViewById<TextView>(R.id.txtgeneraforyou)
        val imgforyou=itemView.findViewById<ImageView>(R.id.imgforyou)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForyouViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.generalforyou_list,parent,false)
        return ForyouViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ForyouViewHolder, position: Int) {
        val title=items[position]
        holder.txtforyou.text=title
        holder.imgforyou.setImageResource(R.drawable.ic_favourite)

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,"This is For you",Toast.LENGTH_SHORT).show()
        }
    }
}