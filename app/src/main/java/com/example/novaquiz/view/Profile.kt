package com.example.novaquiz.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.AccountAdapter
import com.example.novaquiz.adapter.FollowAdapter
import com.example.novaquiz.auth.Login
import com.example.novaquiz.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

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
        followAdapter = FollowAdapter { label ->
            when (label) {
                "Logout" -> {

                    val shp = getSharedPreferences("UserData", MODE_PRIVATE)
                    shp.edit().clear().apply()

                    // Firebase sign out
                    FirebaseAuth.getInstance().signOut()

                    // Redirect to login
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                "Profile" -> {
                    // Already in Profile, maybe show a message or navigate
                }

                "Setting" -> {
//                    val intent = Intent(this, SettingActivity::class.java)
//                    startActivity(intent)
                }
            }
        }
        binding.rvFollow.adapter=followAdapter
        binding.txtDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        binding.btnCopyUserId.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User ID", binding.txtUserId.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "User ID copied to clipboard", Toast.LENGTH_SHORT).show()
        }

    }
}