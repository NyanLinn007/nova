package com.example.novaquiz.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.AccountAdapter
import com.example.novaquiz.adapter.FollowAdapter
import com.example.novaquiz.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    private lateinit var accountAdapter:AccountAdapter
    private lateinit var followAdapter:FollowAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAccount.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        accountAdapter= AccountAdapter()
        binding.rvAccount.adapter=accountAdapter

        binding.rvFollow.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        followAdapter= FollowAdapter()
        binding.rvFollow.adapter=followAdapter
        binding.txtDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

    }
}