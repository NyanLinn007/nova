package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.novaquiz.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream

class ShareBottomSheet(
    private val quoteText: String,
    private val reference: String
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvQuote = view.findViewById<TextView>(R.id.shareQuoteText)
//        val tvRef = view.findViewById<TextView>(R.id.shareReference)
        val shareView = view.findViewById<FrameLayout>(R.id.sharePreviewContainer)

        // Load SharedPreferences
        val shp = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val fontSize = shp.getFloat("fontSize", 16f)
        val fontColor = shp.getString("fontColor", "#FFFFFF") ?: "#FFFFFF"
        val fontAlign = shp.getString("fontAlign", "center") ?: "center"
        val fontStyle = shp.getString("fontStyle", "normal") ?: "normal"
        val fontFamilyResIdString = shp.getString("fontFamily", null)
        val themeUrl = shp.getString("themeByUrl", "")
        val bgColor = shp.getString("themeColor", null)

        // Apply font size & color
        tvQuote.textSize = fontSize
        tvQuote.setTextColor(android.graphics.Color.parseColor(fontColor))

        // Apply font alignment
        when (fontAlign.lowercase()) {
            "start" -> {
                tvQuote.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                tvQuote.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvQuote.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
            "end" -> {
                tvQuote.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                tvQuote.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvQuote.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
            "justify" -> {
                tvQuote.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                tvQuote.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvQuote.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                }
            }
            else -> { // center or unknown
                tvQuote.gravity = Gravity.CENTER
                tvQuote.textAlignment = View.TEXT_ALIGNMENT_CENTER
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvQuote.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
        }

        Log.d("SHARE_ALIGN", "Loaded fontAlign: $fontAlign")



        // Apply text style
        tvQuote.setTypeface(null, when (fontStyle.lowercase()) {
            "bold" -> android.graphics.Typeface.BOLD
            "italic" -> android.graphics.Typeface.ITALIC
            else -> android.graphics.Typeface.NORMAL
        })

        // Apply fontFamily
        fontFamilyResIdString?.toIntOrNull()?.let { resId ->
            val typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), resId)
            tvQuote.typeface = typeface
        }

        // Set background (image or color)
        if (!themeUrl.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(this)
                .load(themeUrl)
                .into(view.findViewById(R.id.shareBackgroundImage)) // ← Your ImageView in layout
        } else if (!bgColor.isNullOrEmpty()) {
            try {
                shareView.setBackgroundColor(android.graphics.Color.parseColor(bgColor))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Set texts
        tvQuote.text = quoteText
//        tvRef.text = reference


        view.findViewById<Button>(R.id.btnSaveImage).setOnClickListener {
            val bitmap = captureBitmapFromView(shareView)
            saveBitmapToGallery(requireContext(), bitmap)
        }
    }


    private fun captureBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "quote_${System.currentTimeMillis()}",
            "Shared from NovaQuiz"
        )
        Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                sheet.setBackgroundResource(R.drawable.bg_bottom_sheet_rounded)

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isFitToContents = false
                behavior.expandedOffset = (resources.displayMetrics.heightPixels * 0.10).toInt()

                // Prevent dragging the sheet itself
                behavior.isDraggable = false

                val params = sheet.layoutParams
                params.height = (resources.displayMetrics.heightPixels * 0.90).toInt()
                sheet.layoutParams = params
            }
        }

        // Allow dismiss when tapping outside
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun getTheme(): Int = R.style.FullScreenBottomSheetDialog
}
