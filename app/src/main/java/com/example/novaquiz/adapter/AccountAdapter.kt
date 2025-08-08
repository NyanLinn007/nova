package com.example.novaquiz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R

class AccountAdapter(private val context: Context):RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val shp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
    private val username = shp.getString("name", "") ?: ""

    private val labels = listOf(username)
    private val startIcon= listOf(
        R.drawable.profile1,
    )
    private val endIcon= listOf(
        R.drawable.arrow1,

    )

    class AccountViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val icon=itemView.findViewById<ImageView>(R.id.startIcon)
        val text=itemView.findViewById<TextView>(R.id.txtLabel)
        val endicon=itemView.findViewById<ImageView>(R.id.endIcon)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountAdapter.AccountViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.list_account,parent,false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountAdapter.AccountViewHolder, position: Int) {
        holder.icon.setImageResource(startIcon[position])
        holder.text.text = labels[position]
        holder.endicon.setImageResource(endIcon[position])

    }

    override fun getItemCount(): Int {
        return labels.size
    }
}