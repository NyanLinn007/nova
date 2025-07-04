package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.fonts.FontStyle
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.font.ResourceFont
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.novaquiz.R
import com.example.novaquiz.databinding.ActivityEditThemeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt


class EditThemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditThemeBinding
    private val MIN_FONT_SIZE = 12
    private val MAX_FONT_SIZE = 40
    private var selectedPhotoUrl: String? = null
    private var selectedBackgroundColorHex: String? = null
    private var selectedBgColor: String? = null
    private var selectedThemeType: ThemeType = ThemeType.NONE  // ✅ default is URL
    private var currentAlign = "center"
    private var selectedFontFamily: String? = null
    private var  selectedFontFamilyResId :String? = null
    enum class ThemeType {
        URL, COLOR, NONE
    }


    private val fontStyles = listOf(
        R.font.kt02,
        R.font.kt03,
        R.font.mm3h,
        R.font.cherry,
        R.font.jasmine,
        R.font.myanmarblack,
        R.font.myanmarangoun,
        R.font.myanmargantgaw,
        R.font.myanmarkuttar,
        R.font.myanmarnayone,
        R.font.myanmarnjaubn,
        R.font.myanmarpaonone,
        R.font.myanmarpixel,
        "sans-serif",
        "serif",
        "monospace",
        "casual",
        "cursive",
        "sans-serif-condensed"
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Load saved settings and apply to UI
        val savedFontSize = sharedPrefs.getFloat("fontSize", 20f)
        binding.txtdefault.textSize = savedFontSize
        binding.fontSeekBar.progress = (savedFontSize - MIN_FONT_SIZE).toInt()

        val savedFontColor = sharedPrefs.getString("fontColor", "#FFFFFF") ?: "#FFFFFF"
        binding.txtdefault.setTextColor(Color.parseColor(savedFontColor))

        currentAlign = sharedPrefs.getString("fontAlign", "center") ?: "center"

        applyAlignment(currentAlign)
        val savedFontStyle = sharedPrefs.getString("fontStyle", "normal") ?: "normal"
        val fontFamilyFromPrefs = sharedPrefs.getString("fontFamily", "sans-serif") ?: "sans-serif"

        val typefaceToApply: Typeface? = try {
            // Try parse fontFamilyFromPrefs as Int (resource ID)
            val resId = fontFamilyFromPrefs.toIntOrNull()
            if (resId != null) {
                ResourcesCompat.getFont(this, resId)
            } else {
                // Use system font name string
                Typeface.create(fontFamilyFromPrefs, if (savedFontStyle == "bold") Typeface.BOLD else Typeface.NORMAL)
            }
        } catch (e: Exception) {
            Typeface.create("sans-serif", if (savedFontStyle == "bold") Typeface.BOLD else Typeface.NORMAL)
        }

        binding.txtdefault.typeface = typefaceToApply ?: Typeface.DEFAULT

        binding.tvStyleToggle.setTypeface(
            null,
            if (savedFontStyle == "bold") Typeface.BOLD else Typeface.NORMAL
        )
        val bgColor = sharedPrefs.getString("themeColor", "") ?: ""
        val savedBackgroundUrl = sharedPrefs.getString("themeByUrl", "") ?: ""

        if (savedBackgroundUrl.isNotEmpty()) {
            // ✅ Use the image background
            selectedPhotoUrl = savedBackgroundUrl
            Glide.with(this)
                .load(savedBackgroundUrl)
                .placeholder(R.drawable.cloudy)
                .error(R.drawable.cloudy)
                .into(binding.backgroundImage)
        } else if (bgColor.isNotEmpty()) {
            // ✅ Use solid color background
            try {
                binding.main.setBackgroundColor(Color.parseColor(bgColor))
                binding.backgroundImage.setImageResource(0) // Clear any image background
            } catch (e: Exception) {
                Log.e("ThemeColor", "Invalid color code: $bgColor")
            }
        } else {
            // ✅ Fallback to default
            binding.backgroundImage.setImageResource(R.drawable.cloudy)
        }


        binding.fontSeekBar.progress = (savedFontSize - MIN_FONT_SIZE).toInt()

        binding.fontSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newFontSize = MIN_FONT_SIZE + progress
                binding.txtdefault.textSize = newFontSize.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val selectedSize = MIN_FONT_SIZE + (seekBar?.progress ?: 0)
                sharedPrefs.edit().putFloat("fontSize", selectedSize.toFloat()).apply()
            }
        })


        val thumbDrawable = ContextCompat.getDrawable(this, R.drawable.custom_thumb)
        binding.fontSeekBar.thumb = thumbDrawable

        binding.btnClose.setOnClickListener {
            navigateBackToMain()
        }

        binding.txtBackground.setOnClickListener {
            // Select txtBackground tab
            binding.txtBackground.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.txtText.setBackgroundResource(R.drawable.bg_tab_unselected)

            // Show background editing UI
            binding.backgroundLayout.visibility = View.VISIBLE
            binding.textLayout.visibility = View.GONE
            binding.fontSeekBar.visibility = View.GONE
            binding.colorScroll.visibility = View.GONE
            binding.fontStyleScroll.visibility = View.GONE
            binding.bgcolorScroll.visibility = View.GONE
        }

        binding.txtText.setOnClickListener {
            // Select txtText tab
            binding.txtText.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.txtBackground.setBackgroundResource(R.drawable.bg_tab_unselected)

            // Show text editing UI
            binding.textLayout.visibility = View.VISIBLE
            binding.backgroundLayout.visibility = View.GONE
            binding.fontSeekBar.visibility = View.VISIBLE
            binding.bgcolorScroll.visibility = View.GONE
            binding.fontStyleScroll.visibility = View.GONE
            binding.colorScroll.visibility = View.GONE
        }


        var isBold = false

        binding.tvStyleToggle.setOnClickListener {
            isBold = !isBold

            // Toggle bold style for center EditText
            binding.txtdefault.setTypeface(null, if (isBold) Typeface.BOLD else Typeface.NORMAL)

            // Optional: visually toggle the "Aa" TextView as well
            binding.tvStyleToggle.setTypeface(null, if (isBold) Typeface.BOLD else Typeface.NORMAL)
            binding.tvStyleToggle.setTypeface(null, if (isBold) Typeface.BOLD else Typeface.NORMAL)
            binding.colorScroll.visibility = View.GONE
            binding.fontStyleScroll.visibility = View.GONE
        }


        binding.imgAlignToggle.setOnClickListener {
            currentAlign = when (currentAlign) {
                "center" -> {
                    applyAlignment("start")
                    "start"
                }
                "start" -> {
                    applyAlignment("end")
                    "end"
                }
                "end" -> {
                    applyAlignment("justify")
                    "justify"
                }
                else -> { // justify or unknown
                    applyAlignment("center")
                    "center"
                }
            }
            binding.colorScroll.visibility = View.GONE
            binding.fontStyleScroll.visibility = View.GONE
        }


        populateFontStyles()
        populateColorOptions()


        binding.fontStyleScroll.visibility = View.GONE
        binding.colorScroll.visibility = View.GONE

        binding.stylefont.setOnClickListener {
            binding.fontStyleScroll.visibility =
                if (binding.fontStyleScroll.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            // Always hide color scroll and bg color scroll when showing font style
            if (binding.fontStyleScroll.visibility == View.VISIBLE) {
                binding.colorScroll.visibility = View.GONE
                binding.bgcolorScroll.visibility = View.GONE
            }
        }






        binding.imgColorWheel.setOnClickListener {
            binding.colorScroll.visibility =
                if (binding.colorScroll.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            // Always hide font style when showing colorScroll
            if (binding.colorScroll.visibility == View.VISIBLE) {
                binding.fontStyleScroll.visibility = View.GONE
            }
        }


        binding.bgcolorwheel.setOnClickListener {
            binding.bgcolorScroll.visibility =
                if (binding.bgcolorScroll.visibility == View.VISIBLE) View.GONE else View.VISIBLE


            if (binding.bgcolorContainer.childCount == 0) {
                populateBackgroundColorOptions()
            }
        }

        binding.bgGallary.setOnClickListener {
            if (binding.bgcolorScroll.visibility == View.VISIBLE) {
                binding.bgcolorScroll.visibility = View.GONE
            }

            val unsplashSheet = UnsplashBottomSheet()
            unsplashSheet.setOnPhotoSelectedListener(object :
                UnsplashBottomSheet.OnPhotoSelectedListener {
                override fun onPhotoSelected(photoUrl: String) {
                    selectedPhotoUrl = photoUrl
                    Glide.with(this@EditThemeActivity)
                        .load(photoUrl)

                        .thumbnail(0.1f) // Low-res preview while full image loads
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and transformed
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade()) // Smooth fade-in
                        .placeholder(R.drawable.loading_placeholder) // Optional placeholder while loading
                        .into(binding.backgroundImage) // Make sure this is your ImageView
                }
            })
            unsplashSheet.show(supportFragmentManager, "UnsplashSheet")
        }

        binding.bgGallary.setOnClickListener {
            val unsplashSheet = UnsplashBottomSheet()
            unsplashSheet.setOnPhotoSelectedListener(object :
                UnsplashBottomSheet.OnPhotoSelectedListener {
                override fun onPhotoSelected(photoUrl: String) {
                    selectedPhotoUrl = photoUrl  // Save here
                    selectedThemeType = ThemeType.URL
                    selectedBgColor = ""
                    binding.main.setBackgroundColor(Color.TRANSPARENT)


                    Glide.with(this@EditThemeActivity)
                        .load(photoUrl)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.loading_placeholder)
                        .into(binding.backgroundImage)
                }
            })
            unsplashSheet.show(supportFragmentManager, "UnsplashSheet")
        }
        binding.donebutton.setOnClickListener {
            try {
                Log.d("ThemeSave", "Save button clicked")

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    Log.e("ThemeSave", "User not logged in")
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Log.d("ThemeSave", "User ID: $userId")

                val fontFamily = selectedFontFamilyResId ?: selectedFontFamily ?: "sans-serif"

                val sharedPrefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                val savedFontSize = sharedPrefs.getFloat("fontSize", -1f)
                val savedFontColor = sharedPrefs.getString("fontColor", "") ?: ""
                val savedAlign = sharedPrefs.getString("fontAlign", "") ?: ""
                val savedStyle = sharedPrefs.getString("fontStyle", "") ?: ""
                val savedColor = sharedPrefs.getString("themeColor", "") ?: ""
                val savedUrl = sharedPrefs.getString("themeByUrl", "") ?: ""
                val savedFamily = sharedPrefs.getString("fontFamily", "sans-serif") ?: "sans-serif"

                val scaledDensity = resources.displayMetrics.scaledDensity
                val fontSizeSp = (binding.txtdefault.textSize / scaledDensity)
                val fontSizeRounded = fontSizeSp.roundToInt()

                val fontColorInt = binding.txtdefault.currentTextColor
                val fontColorHex = String.format("#%06X", 0xFFFFFF and fontColorInt)

                val align = currentAlign
                val isBold = binding.txtdefault.typeface?.isBold ?: false
                val fontStyle = if (isBold) "bold" else "normal"

                val themeColor = if (selectedThemeType == ThemeType.COLOR && !selectedBgColor.isNullOrBlank())
                    selectedBgColor!! else savedColor

                val themeByUrl = if (selectedThemeType == ThemeType.URL && !selectedPhotoUrl.isNullOrBlank())
                    selectedPhotoUrl!! else savedUrl

                Log.d("ThemeSave", "Current values -> FontSize: $fontSizeRounded, FontColor: $fontColorHex, Align: $align, Style: $fontStyle, ThemeColor: $themeColor, ThemeURL: $themeByUrl")

                val themeData = mutableMapOf<String, Any>()

                // ✅ Compare rounded int values and store as Long (Firestore stores numbers as Long by default)

                    themeData["fontSize"] = fontSizeSp



                if (fontColorHex != savedFontColor) themeData["fontColor"] = fontColorHex
                if (align != savedAlign) themeData["fontAlign"] = align
                if (fontStyle != savedStyle) themeData["fontStyle"] = fontStyle
                if (themeColor != savedColor) themeData["themeColor"] = themeColor
                if (themeByUrl != savedUrl) themeData["themeByUrl"] = themeByUrl
                if (fontFamily != savedFamily) {
                    themeData["fontFamily"] = fontFamily
                }

                if (themeData.isEmpty()) {
                    Log.d("ThemeSave", "No changes detected. Skipping Firestore update.")
                    Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Log.d("ThemeSave", "Changed fields to update in Firestore: $themeData")

                val db = FirebaseFirestore.getInstance()
                db.collection("Users").document(userId)
                    .update(themeData)
                    .addOnSuccessListener {
                        Log.d("ThemeSave", "Firestore update successful")

                        val editor = sharedPrefs.edit()
                        editor.putFloat("fontSize", fontSizeRounded.toFloat()) // Save as float locally
                        editor.putString("fontColor", fontColorHex)
                        editor.putString("fontAlign", align)
                        editor.putString("fontStyle", fontStyle)
                        editor.putString("themeColor", themeColor)
                        editor.putString("themeByUrl", themeByUrl)
                        editor.putString("fontFamily", fontFamily)
                        editor.apply()

                        Log.d("ThemeSave", "SharedPreferences updated")
                        Toast.makeText(this, "Theme saved successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ThemeSave", "Failed to save theme to Firestore: ${e.message}", e)
                        Toast.makeText(this, "Failed to save theme: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Log.e("ThemeSave", "Unexpected error: ${e.message}", e)
                Toast.makeText(this, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }



    }
    private fun applyAlignment(align: String) {
        currentAlign = align
        when (align) {
            "start" -> {
                binding.txtdefault.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                binding.imgAlignToggle.setImageResource(R.drawable.left_menu)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.txtdefault.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
            "end" -> {
                binding.txtdefault.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                binding.imgAlignToggle.setImageResource(R.drawable.right_menu)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.txtdefault.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
            "justify" -> {
                // Justification supported only on API 26+
                binding.txtdefault.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                binding.imgAlignToggle.setImageResource(R.drawable.menu) // <-- Your justify icon here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.txtdefault.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                }
            }
            else -> { // center
                binding.txtdefault.gravity = Gravity.CENTER
                binding.imgAlignToggle.setImageResource(R.drawable.center_align)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.txtdefault.justificationMode = LineBreaker.JUSTIFICATION_MODE_NONE
                }
            }
        }
    }



    private fun populateFontStyles() {
        val fontStyleContainer = binding.fontStyleContainer
        val txtCenter = binding.txtdefault

        fontStyleContainer.removeAllViews()

        fontStyles.forEach { fontStyle ->
            // Display name for UI only
            val fontName = if (fontStyle is Int) {
                try {
                    resources.getResourceEntryName(fontStyle)
                } catch (e: Resources.NotFoundException) {
                    "UnknownFont"
                }
            } else {
                fontStyle.toString()
            }

            val fontCircle = TextView(this).apply {
                text = fontName
                setTextColor(Color.WHITE)
                textSize = 16f
                gravity = Gravity.CENTER

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(12, 4, 12, 4)
                }

                setPadding(15, 0, 15, 0)
                background = ContextCompat.getDrawable(this@EditThemeActivity, R.drawable.fontstyle_bg)

                // Preview font on button
                typeface = when (fontStyle) {
                    is Int -> ResourcesCompat.getFont(context, fontStyle) ?: Typeface.DEFAULT
                    is String -> Typeface.create(fontStyle, Typeface.NORMAL) ?: Typeface.DEFAULT
                    else -> Typeface.DEFAULT
                }

                setOnClickListener {
                    val selectedTypeface = when (fontStyle) {
                        is Int -> ResourcesCompat.getFont(context, fontStyle) ?: Typeface.DEFAULT
                        is String -> Typeface.create(fontStyle, Typeface.NORMAL) ?: Typeface.DEFAULT
                        else -> Typeface.DEFAULT
                    }

                    txtCenter.typeface = selectedTypeface

                    // 🟢 Save BOTH type and value
                    if (fontStyle is Int) {
                         selectedFontFamilyResId = fontStyle.toString()
                        selectedFontFamily = null // clear string

                        Log.d("fontName", "Selected resource font ID: $selectedFontFamilyResId")
                    } else if (fontStyle is String) {
                        selectedFontFamily = fontStyle
                        var selectedFontFamilyResId = null // clear resId

                        Log.d("fontName", "Selected system font name: $selectedFontFamily")
                    }
                }
            }

            fontStyleContainer.addView(fontCircle)
        }
    }







    private fun populateColorOptions() {
        val colorOptions = listOf(
            "#FFFFFF", "#000000", "#FF0000", "#00FF00", "#0000FF",
            "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#A52A2A"
        )

        val colorContainer = binding.colorContainer
        val txtCenter = binding.txtdefault

        val widthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics
        ).toInt()

        val heightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics
        ).toInt()

        colorContainer.removeAllViews()

        colorOptions.forEach { hex ->
            val drawable = ContextCompat.getDrawable(this@EditThemeActivity, R.drawable.circle_bg)?.mutate()
            drawable?.setTint(Color.parseColor(hex))

            val colorView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(widthPx, heightPx).apply {
                    setMargins(0, 4, 12, 4)
                }
                background = drawable

                setOnClickListener {
                    txtCenter.setTextColor(Color.parseColor(hex))
                }
            }

            colorContainer.addView(colorView)
        }
    }

    private fun populateBackgroundColorOptions() {
        val colorOptions = listOf(
            "#FFFFFF", "#000000", "#FF0000", "#00FF00", "#0000FF",
            "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#A52A2A"
        )

        val bgcolorContainer = binding.bgcolorContainer  // your LinearLayout inside HorizontalScrollView
        val backgroundLayout = binding.main            // layout whose background will change

        val widthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics
        ).toInt()

        val heightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics
        ).toInt()

        bgcolorContainer.removeAllViews()

        colorOptions.forEach { hex ->
            val drawable = ContextCompat.getDrawable(this, R.drawable.circle_bg)?.mutate()
            drawable?.setTint(Color.parseColor(hex))

            val colorView = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(widthPx, heightPx).apply {
                    setMargins(12, 4, 12, 4)
                }
                background = drawable

                setOnClickListener {
                    backgroundLayout.setBackgroundColor(Color.parseColor(hex))

                    // ✅ Store selected color in variable
                    selectedBgColor = hex
                    selectedThemeType = ThemeType.COLOR
                    selectedPhotoUrl=""
                }
            }

            bgcolorContainer.addView(colorView)
        }
    }





    private fun navigateBackToMain() {
            val intent = Intent(this, Theme::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }
