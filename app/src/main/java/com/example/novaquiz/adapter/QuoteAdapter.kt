package com.example.novaquiz.adapter

import android.graphics.pdf.models.ListItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R
import com.example.novaquiz.data.Quote

class QuoteAdapter( private var quotes:List<Quote>):RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>(){
    class QuoteViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
       var tvText=itemView.findViewById<TextView>(R.id.tvText)
        var tvReference=itemView.findViewById<TextView>(R.id.tvReference)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        return QuoteViewHolder(view)
    }

    override fun getItemCount(): Int {
       return quotes.size
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
       var quote =quotes[position]
        holder.tvText.text=quote.text
        holder.tvReference.text=quote.reference
    }
}