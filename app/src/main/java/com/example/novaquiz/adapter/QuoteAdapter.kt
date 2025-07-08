package com.example.novaquiz.adapter



import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.novaquiz.R
import com.example.novaquiz.data.Quote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class QuoteAdapter(private var quotes: List<Quote>,
                    private val fontsize:Float,
                    private val fontColor: String,
                    private val fontAlign: String,
                    private val fontStyle: String,
                    private val fontFamily: Typeface


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
        holder.tvText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontsize)
        holder.tvReference.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, fontsize * 0.8f)

        try {
            val parsedColor = Color.parseColor(fontColor)
            holder.tvText.setTextColor(parsedColor)
            holder.tvReference.setTextColor(parsedColor)
        } catch (e: Exception) {
            holder.tvText.setTextColor(Color.BLACK)
            holder.tvReference.setTextColor(Color.GRAY)
        }

        val align = fontAlign.lowercase()
        val gravity = when (align) {
            "start" -> Gravity.START
            "end" -> Gravity.END
            else -> Gravity.CENTER
        }
        val textAlignment = when (align) {
            "start" -> View.TEXT_ALIGNMENT_VIEW_START
            "end" -> View.TEXT_ALIGNMENT_VIEW_END
            else -> View.TEXT_ALIGNMENT_CENTER
        }

// Apply to tvText
        holder.tvText.gravity = gravity
        holder.tvText.textAlignment = textAlignment

// Apply to tvReference
        holder.tvReference.gravity = gravity
        holder.tvReference.textAlignment = textAlignment

        val style = when (fontStyle.lowercase()) {
            "bold" -> Typeface.BOLD
            "italic" -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        holder.tvText.typeface = Typeface.create(fontFamily, style)
        holder.tvReference.typeface = Typeface.create(fontFamily, style)



//        val scale = holder.itemView.resources.displayMetrics.density
//        val sizePx = (fontsize * 3 * scale).toInt()

//        holder.btnShare.layoutParams.width = sizePx
//        holder.btnShare.layoutParams.height = sizePx
//        holder.btnFavorite.layoutParams.width = sizePx
//        holder.btnFavorite.layoutParams.height = sizePx

//        try {
//            val tintColor = Color.parseColor(fontColor)
//            holder.btnShare.setColorFilter(tintColor)
//            holder.btnFavorite.setColorFilter(tintColor)
//        } catch (e: Exception) {
//            holder.btnShare.setColorFilter(Color.BLACK)
//            holder.btnFavorite.setColorFilter(Color.BLACK)
//        }

        holder.btnFavorite.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid

            if (userId != null && quote.quoteId.isNotEmpty()) {
                val favoriteData = hashMapOf(
                    "userId" to userId,
                    "quoteId" to quote.quoteId
                )

                db.collection("Favorite")
                    .add(favoriteData)
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Added to favorites", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "User not logged in or quoteId missing", Toast.LENGTH_SHORT).show()
            }
        }


    }


}