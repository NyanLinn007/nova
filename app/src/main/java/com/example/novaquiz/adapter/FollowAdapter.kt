package com.example.novaquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R
import com.example.novaquiz.adapter.AccountAdapter.AccountViewHolder

class FollowAdapter:RecyclerView.Adapter<FollowAdapter.FollowViewHolder>() {
    private val lables= listOf("Profile","Setting","Logout")
    private val startIcon= listOf(
        R.drawable.ic_favourite,
        R.drawable.ic_favourite,
        R.drawable.ic_favourite
    )
    private val endIcon= listOf(
        R.drawable.ic_close,
        R.drawable.ic_close,
        R.drawable.ic_close
    )
    class FollowViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val start=itemView.findViewById<ImageView>(R.id.start)
        val txtfollow=itemView.findViewById<TextView>(R.id.txtfollow)
        val end=itemView.findViewById<ImageView>(R.id.end)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowAdapter.FollowViewHolder {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.list_follow,parent,false)
        return FollowViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowAdapter.FollowViewHolder, position: Int) {
        holder.start.setImageResource(startIcon[ position])
        holder.txtfollow.text=lables[position]
        holder.end.setImageResource(endIcon[ position])
    }

    override fun getItemCount(): Int {
       return lables.size
    }

}