package com.example.novaquiz.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.novaquiz.R
import com.example.novaquiz.data.Themes

class ForyouAdapter(private val themes:List<Themes>):RecyclerView.Adapter<ForyouAdapter.ForyouViewHolder> (){
    class ForyouViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        var imgforyou=itemView.findViewById<ImageView>(R.id.imgforyou)
        var txtforyou=itemView.findViewById<TextView>(R.id.txtforyou)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForyouAdapter.ForyouViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.list_foryou,parent,false)
        return ForyouViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForyouAdapter.ForyouViewHolder, position: Int) {

       var item=themes[position]
        Glide.with(holder.imgforyou.context)
            .load(item.backgroundUrl)
            .placeholder(R.drawable.foryou_bg)
            .into(holder.imgforyou)

        holder.txtforyou.text=item.category

        holder.txtforyou.gravity = when (item.fontAlign.lowercase()) {
            "center" -> Gravity.CENTER
            "left" -> Gravity.START
            "right" -> Gravity.END
            else -> Gravity.CENTER
        }
        try {
            if (item.fontColor.isNotEmpty()) {
                holder.txtforyou.setTextColor(Color.parseColor(item.fontColor))
            } else {
                holder.txtforyou.setTextColor(Color.WHITE)
            }
        } catch (e: Exception) {
            holder.txtforyou.setTextColor(Color.BLACK)
        }

        val fontSizeSp = item.fontSize.removeSuffix("px").toFloatOrNull() ?: 16f
        holder.txtforyou.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp)

        // Font style (bold, italic, normal)
        when (item.fontStyle.lowercase()) {
            "bold" -> holder.txtforyou.setTypeface(null, Typeface.BOLD)
            "italic" -> holder.txtforyou.setTypeface(null, Typeface.ITALIC)
            else -> holder.txtforyou.setTypeface(null, Typeface.NORMAL)
        }

    }

    override fun getItemCount(): Int {
       return themes.size
    }
}