package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.novaquiz.R
import com.example.novaquiz.adapter.QuoteAdapter
import com.example.novaquiz.data.Quote
import com.example.novaquiz.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val quoteList = mutableListOf<Quote>()
    private lateinit var adapter: QuoteAdapter

    private lateinit var webViewContainer: View
    private lateinit var webViewYoutube: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize WebView container and WebView
        webViewContainer = findViewById(R.id.webViewContainer)
        webViewYoutube = findViewById(R.id.webViewYoutube)

        // Setup WebView
        webViewYoutube.settings.javaScriptEnabled = true
        webViewYoutube.settings.domStorageEnabled = true
        webViewYoutube.settings.cacheMode = WebSettings.LOAD_DEFAULT

        webViewYoutube.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        // Load YouTube Music URL once
        webViewYoutube.loadUrl("https://www.youtube.com/")

        // Existing code for preferences, background, adapter setup...
        val shp = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val fontsize = shp.getFloat("fontSize", 16f)
        val fontColor = shp.getString("fontColor", "#000000") ?: "#000000"
        val fontAlign = shp.getString("fontAlign", "center") ?: "center"
        val fontStyle = shp.getString("fontStyle", "normal") ?: "normal"
        val themeUrl = shp.getString("themeByUrl", "") ?: ""
        val bgColor = shp.getString("themeColor", null)
        val fontFamily = shp.getString("fontFamily", "sans-serif") ?: "sans-serif"

        if (themeUrl.isNotEmpty()) {
            Glide.with(this)
                .load(themeUrl)
                .placeholder(R.drawable.cloudy)
                .error(R.drawable.cloudy)
                .into(binding.backgroundImage)
        } else if (!bgColor.isNullOrEmpty()) {
            try {
                binding.main.setBackgroundColor(Color.parseColor(bgColor))
                binding.backgroundImage.setImageResource(0)
            } catch (e: Exception) {
                Log.e("ThemeColor", "Invalid color code: $bgColor")
            }
        } else {
            binding.backgroundImage.setImageResource(R.drawable.cloudy)
        }

        adapter = QuoteAdapter(quoteList, fontsize, fontColor, fontAlign, fontStyle, fontFamily)
        binding.rvQuote.layoutManager = LinearLayoutManager(this)
        binding.rvQuote.adapter = adapter
        binding.rvQuote.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        PagerSnapHelper().attachToRecyclerView(binding.rvQuote)

        showLoading(true)
        if (isInternetAvailable()) {
            fetchQuotes()
        } else {
            showLoading(false)
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
        }

        // Button listeners for theme, general, profile
        binding.btnTheme.setOnClickListener { vibrateAndLaunchActivity(Theme::class.java) }
        binding.btnGeneral.setOnClickListener { vibrateAndLaunchActivity(General::class.java) }
        binding.btnProfile.setOnClickListener { vibrateAndLaunchActivity(Profile::class.java, finishAfter = false) }

        // Music button toggles WebView container visibility
        binding.btnmusic.setOnClickListener {
            webViewContainer.visibility = if (webViewContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Hide WebView container when clicking outside WebView
        webViewContainer.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val location = IntArray(2)
                webViewYoutube.getLocationOnScreen(location)
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()

                val left = location[0]
                val top = location[1]
                val right = left + webViewYoutube.width
                val bottom = top + webViewYoutube.height

                if (x < left || x > right || y < top || y > bottom) {
                    webViewContainer.visibility = View.GONE
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Override back press to close WebView if open
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webViewContainer.visibility == View.VISIBLE) {
                    webViewContainer.visibility = View.GONE
                } else {
                    finishAffinity()
                }
            }
        })
    }

    private fun vibrateAndLaunchActivity(activityClass: Class<*>, finishAfter: Boolean = true) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
        startActivity(Intent(this, activityClass))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        if (finishAfter) finish()
    }

    private fun fetchQuotes() {
        db.collection("Quotes")
            .get()
            .addOnSuccessListener { result ->
                quoteList.clear()
                for (document in result) {
                    val quote = Quote(
                        text = document.getString("text") ?: "",
                        reference = document.getString("reference") ?: ""
                    )
                    quoteList.add(quote)
                }
                adapter.notifyDataSetChanged()
                showLoading(false)
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(this, "Failed to fetch quotes: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("MainActivity", "Firestore fetch error", exception)
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvQuote.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
