package com.example.novaquiz.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.novaquiz.R
import com.example.novaquiz.auth.Login
import com.example.novaquiz.view.MainActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var logo:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logo=findViewById(R.id.logo)

        val zoomIn= AnimationUtils.loadAnimation(this,R.anim.zoom_in)
        logo.startAnimation(zoomIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val shp = getSharedPreferences("UserData", MODE_PRIVATE)
            val isLoggedIn = shp.getBoolean("Login", false)
            val email = shp.getString("email", null)

            if (isLoggedIn && !email.isNullOrEmpty()) {
                // Already logged in, go to main page
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Not logged in yet, go to login
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 3000)

        }
    }
