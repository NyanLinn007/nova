package com.example.novaquiz.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R
import com.example.novaquiz.view.EditThemeActivity

class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

    private val titles = listOf("Plus", "Edit", "All", "Free", "New", "Most Popular", "Recent")

    class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.btnTheme)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_theme, parent, false)
        return ThemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val title = titles[position]
        holder.button.text=title
        holder.button.setOnClickListener {
            if(title=="Edit"){
                val context = holder.itemView.context
                val intent = Intent(context, EditThemeActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}
