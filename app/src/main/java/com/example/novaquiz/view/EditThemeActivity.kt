package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.novaquiz.R
import com.example.novaquiz.databinding.ActivityEditThemeBinding


class EditThemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditThemeBinding
    private val MIN_FONT_SIZE = 12
    private val MAX_FONT_SIZE = 40

    private val fontStyles = listOf(
        "sans-serif",       // Android default Roboto
        "serif",            // Times New Roman like
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
        val currentFontSize = sharedPrefs.getFloat("fontSize", 20f)
        binding.txtdefault.textSize = currentFontSize

        binding.fontSeekBar.progress = (currentFontSize - MIN_FONT_SIZE).toInt()

        binding.fontSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newFontSize = MIN_FONT_SIZE + progress
                binding.txtdefault.textSize = newFontSize.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val selectedSize = MIN_FONT_SIZE + seekBar!!.progress
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


        binding.txtdefault.gravity = Gravity.CENTER

        var currentAlign = "center"

        binding.imgAlignToggle.setOnClickListener {
            when (currentAlign) {
                "center" -> {
                    currentAlign = "start"
                    binding.txtdefault.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    binding.imgAlignToggle.setImageResource(R.drawable.left_menu)
                }

                "start" -> {
                    currentAlign = "end"
                    binding.txtdefault.gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    binding.imgAlignToggle.setImageResource(R.drawable.right_menu)
                }

                "end" -> {
                    currentAlign = "center"
                    binding.txtdefault.gravity = Gravity.CENTER
                    binding.imgAlignToggle.setImageResource(R.drawable.menu)

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

            // Show the Unsplash image picker
            UnsplashBottomSheet().show(supportFragmentManager, "UnsplashSheet")
        }

    }

    private fun populateFontStyles() {
        val fontStyleContainer = binding.fontStyleContainer
        val txtCenter = binding.txtdefault


        fontStyleContainer.removeAllViews()

        fontStyles.forEach { fontName ->
            val fontCircle = TextView(this).apply {
                text = fontName
                setTextColor(Color.WHITE)
                textSize = 16f
                gravity = Gravity.CENTER
                typeface = Typeface.create(fontName, Typeface.NORMAL)

                layoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(12, 4, 12, 4)
                    setPadding(15,0,15,0)
                }
                // Apply the same circle background as the color picker
                background = ContextCompat.getDrawable(this@EditThemeActivity, R.drawable.fontstyle_bg)

                setOnClickListener {
                    txtCenter.typeface = Typeface.create(fontName, Typeface.NORMAL)

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
                    backgroundLayout.setBackgroundColor(Color.parseColor(hex)) // ✅ Set background
                                // ✅ Hide color picker
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
