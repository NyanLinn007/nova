package com.example.novaquiz.adapter



import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R
import com.example.novaquiz.data.Quote




class QuoteAdapter( private var quotes: List<Quote>,
                    private val fontsize:Float,
                    private val fontColor: String,
                    private val fontAlign: String,
                    private val fontStyle: String,


):RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>(){
    class QuoteViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
       var tvText=itemView.findViewById<TextView>(R.id.tvText)
        var tvReference=itemView.findViewById<TextView>(R.id.tvReference)
        var btnShare=itemView.findViewById<ImageButton>(R.id.btnShare)
        var btnFavorite=itemView.findViewById<ImageButton>(R.id.btnFavorite)
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
        holder.tvText.textSize=fontsize
        holder.tvReference.textSize = fontsize * 0.8f
        try {
            val parsedColor = Color.parseColor(fontColor)
            holder.tvText.setTextColor(parsedColor)
            holder.tvReference.setTextColor(parsedColor)
        } catch (e: Exception) {
            holder.tvText.setTextColor(Color.BLACK)
            holder.tvReference.setTextColor(Color.GRAY)
        }

        val alignment = when (fontAlign) {
            "center" -> View.TEXT_ALIGNMENT_CENTER
            "start" -> View.TEXT_ALIGNMENT_TEXT_START
            "end" -> View.TEXT_ALIGNMENT_TEXT_END
            else -> View.TEXT_ALIGNMENT_CENTER
        }
        holder.tvText.textAlignment = alignment
        holder.tvReference.textAlignment = alignment

        val style = when (fontStyle.lowercase()) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        holder.tvText.setTypeface(null, style)
        holder.tvReference.setTypeface(null, style)

        val scale = holder.itemView.resources.displayMetrics.density
        val sizePx = (fontsize * 3 * scale).toInt()

        holder.btnShare.layoutParams.width = sizePx
        holder.btnShare.layoutParams.height = sizePx
        holder.btnFavorite.layoutParams.width = sizePx
        holder.btnFavorite.layoutParams.height = sizePx

        try {
            val tintColor = Color.parseColor(fontColor)
            holder.btnShare.setColorFilter(tintColor)
            holder.btnFavorite.setColorFilter(tintColor)
        } catch (e: Exception) {
            holder.btnShare.setColorFilter(Color.BLACK)
            holder.btnFavorite.setColorFilter(Color.BLACK)
        }

    }


}