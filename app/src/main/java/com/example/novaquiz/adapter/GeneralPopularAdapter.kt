package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R

class GeneralPopularAdapter: RecyclerView.Adapter<GeneralPopularAdapter.GeneralViewHolder>() {

    private val items = listOf(
        "Popular 1",
        "Popular 2",
        "Popular 3",
        "Popular 4"
    )
    class GeneralViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val txtpopular=itemView.findViewById<TextView>(R.id.txtpopular)
        val icon=itemView.findViewById<ImageView>(R.id.generalicon)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GeneralPopularAdapter.GeneralViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.mostpopular_list,parent,false)
        return GeneralViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeneralPopularAdapter.GeneralViewHolder, position: Int) {
        val title=items[position]
        holder.txtpopular.text=title
        holder.icon.setImageResource(R.drawable.rightarrow)

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,"This is Popular",Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
     return items.size
    }
}