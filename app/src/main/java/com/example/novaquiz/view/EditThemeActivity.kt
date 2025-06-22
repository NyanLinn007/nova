package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.novaquiz.R
import com.example.novaquiz.databinding.ActivityEditThemeBinding

class EditThemeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditThemeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivityEditThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fontSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fontSize = 12 + progress // Maps 0–28 to 12–40
                binding.txtdefault.textSize = fontSize.toFloat()
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val thumbDrawable = ContextCompat.getDrawable(this, R.drawable.custom_thumb)
        binding.fontSeekBar.thumb=thumbDrawable

        binding.btnClose.setOnClickListener {
            navigateBackToMain()
        }

        binding.txtBackground.setOnClickListener {
            binding.txtBackground.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.txtText.setBackgroundResource(R.drawable.bg_tab_unselected)

            binding.backgroundLayout.visibility = View.VISIBLE
            binding.textLayout.visibility = View.GONE
            binding.fontSeekBar.visibility = View.GONE


        }

        binding.txtText.setOnClickListener {
            binding.txtBackground.setBackgroundResource(R.drawable.bg_tab_unselected)
            binding.txtText.setBackgroundResource(R.drawable.bg_tab_selected)

            binding.textLayout.visibility = View.VISIBLE
            binding.backgroundLayout.visibility = View.GONE
            binding.fontSeekBar.visibility = View.VISIBLE


        }
//        binding.fontSlider.addOnChangeListener { _, value, _ ->
//            binding.txtdefault.textSize = value
//        }

    }
    private fun navigateBackToMain() {
        val intent = Intent(this, Theme::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}