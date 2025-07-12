package com.example.novaquiz.view

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.novaquiz.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class ShareBottomSheet(
    private val quoteId:String,
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
        val btnCopy = view.findViewById<MaterialButton>(R.id.btnCopy)
        val myfav=view.findViewById<MaterialButton>(R.id.myfav)
        val ivFacebookLogo = view.findViewById<ImageView>(R.id.ivFacebookLogo)
        val ivInstagramLogo=view.findViewById<ImageView>(R.id.ivInstagramLogo)
        val ivTelegramLogo=view.findViewById<ImageView>(R.id.ivTelegramLogo)
        val ivMessengerLogo=view.findViewById<ImageView>(R.id.ivMessengerLogo)
        val ivellipsis=view.findViewById<ImageView>(R.id.ivellipsis)

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
        btnCopy.setOnClickListener {
            val textToCopy = tvQuote.text.toString()

            // Copy to clipboard
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Quote", textToCopy)
            clipboard.setPrimaryClip(clip)

            // Optional: Toast message
            Toast.makeText(requireContext(), "Quote copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

        myfav.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid

            if (userId != null && quoteId.isNotEmpty()) {
                val docId = "${userId}_${quoteId}"
                val favoriteRef = db.collection("Favorite").document(docId)

                favoriteRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Toast.makeText(requireContext(), "Already added to favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            val favoriteData = hashMapOf(
                                "userId" to userId,
                                "quoteId" to quoteId
                            )

                            favoriteRef.set(favoriteData)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "User not logged in or quoteId missing", Toast.LENGTH_SHORT).show()
            }
        }
        ivFacebookLogo.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, quoteText)

            // Specify Facebook package to share directly to Facebook app
            shareIntent.setPackage("com.facebook.katana")

            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND)
                fallbackIntent.type = "text/plain"
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, quoteText)
                startActivity(Intent.createChooser(fallbackIntent, "Share via"))
            }
        }

        ivInstagramLogo.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, quoteText)

            // Specify Facebook package to share directly to Facebook app
            shareIntent.setPackage("com.instagram.android")

            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND)
                fallbackIntent.type = "text/plain"
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, quoteText)
                startActivity(Intent.createChooser(fallbackIntent, "Share via"))
            }
        }

        ivTelegramLogo.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, quoteText)


            shareIntent.setPackage("org.telegram.messenger")

            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND)
                fallbackIntent.type = "text/plain"
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, quoteText)
                startActivity(Intent.createChooser(fallbackIntent, "Share via"))
            }
        }

        ivMessengerLogo.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, quoteText)


            shareIntent.setPackage("com.facebook.orca")
            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND)
                fallbackIntent.type = "text/plain"
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, quoteText)
                startActivity(Intent.createChooser(fallbackIntent, "Share via"))
            }
        }

        ivellipsis.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, quoteText)


            shareIntent.setPackage("com.zhiliaoapp.musically")
            try {
                startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                val fallbackIntent = Intent(Intent.ACTION_SEND)
                fallbackIntent.type = "text/plain"
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, quoteText)
                startActivity(Intent.createChooser(fallbackIntent, "Share via"))
            }
        }



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
