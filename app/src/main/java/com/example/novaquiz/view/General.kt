package com.example.novaquiz.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.GeneralForyouAdapter
import com.example.novaquiz.adapter.GeneralPopularAdapter
import com.example.novaquiz.databinding.ActivityGeneralBinding

class General : AppCompatActivity() {
    private lateinit var binding:ActivityGeneralBinding
    private lateinit var GeneralPopularAdapter:GeneralPopularAdapter
    private lateinit var GeneralForyouAdapter:GeneralForyouAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivityGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.rvgeneral.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        GeneralPopularAdapter=GeneralPopularAdapter()
        binding.rvgeneral.adapter=GeneralPopularAdapter

        binding.rvforyou.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        GeneralForyouAdapter= GeneralForyouAdapter()
        binding.rvforyou.adapter=GeneralForyouAdapter

        binding.btnclose.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            navigateBackToMain()

        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToMain()
            }
        })



        }
    private fun navigateBackToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
    }
