package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.bumptech.glide.Glide
import com.example.novaquiz.R
import com.example.novaquiz.adapter.QuoteAdapter
import com.example.novaquiz.data.Quote
import com.example.novaquiz.databinding.ActivityMainBinding
import com.example.novaquiz.api.OnFavoriteQuotesFetched
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), OnFavoriteQuotesFetched {

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

        // WebView init
        webViewContainer = findViewById(R.id.webViewContainer)
        webViewYoutube = findViewById(R.id.webViewYoutube)

        webViewYoutube.settings.javaScriptEnabled = true
        webViewYoutube.settings.domStorageEnabled = true
        webViewYoutube.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webViewYoutube.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }
        webViewYoutube.loadUrl("https://www.youtube.com/")

        // Load Preferences
        val shp = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val fontsize = shp.getFloat("fontSize", 16f)
        val fontColor = shp.getString("fontColor", "#000000") ?: "#000000"
        val fontAlign = shp.getString("fontAlign", "center") ?: "center"
        val fontStyle = shp.getString("fontStyle", "normal") ?: "normal"
        val themeUrl = shp.getString("themeByUrl", "") ?: ""
        val bgColor = shp.getString("themeColor", null)
        val fontFamilyResIdString = shp.getString("fontFamily", null)

        // Background setup
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

        // Font typeface
        val fontTypeface: Typeface = try {
            val fontResId = fontFamilyResIdString?.toIntOrNull() ?: 0
            if (fontResId != 0) {
                ResourcesCompat.getFont(this, fontResId) ?: Typeface.DEFAULT
            } else {
                Typeface.DEFAULT
            }
        } catch (e: Exception) {
            Log.e("FontLoad", "Failed to load font from ID: $fontFamilyResIdString", e)
            Typeface.DEFAULT
        }

        // RecyclerView
        adapter = QuoteAdapter(quoteList, fontsize, fontColor, fontAlign, fontStyle, fontTypeface)
        binding.rvQuote.layoutManager = LinearLayoutManager(this)
        binding.rvQuote.adapter = adapter
        PagerSnapHelper().attachToRecyclerView(binding.rvQuote)

        showLoading(true)
        if (isInternetAvailable()) {
            fetchQuotes()
        } else {
            showLoading(false)
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
        }

        // Button actions
        binding.btnTheme.setOnClickListener { vibrateAndLaunchActivity(Theme::class.java) }
        binding.btnProfile.setOnClickListener { vibrateAndLaunchActivity(Profile::class.java, false) }

        binding.btnGeneral.setOnClickListener {
            vibrate()
            val generalSheet = General(this)
            generalSheet.show(supportFragmentManager, "GeneralBottomSheet")
        }

        binding.btnmusic.setOnClickListener {
            webViewContainer.visibility = if (webViewContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

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

    private fun fetchQuotes() {
        db.collection("Quotes")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                quoteList.clear()
                for (document in result) {
                    val quote = Quote(
                        text = document.getString("text") ?: "",
                        reference = document.getString("reference") ?: "",
                        Category = document.getString("Category") ?: "",
                        createdAt = document.getString("createdAt") ?: "",
                        quoteId = document.id
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

    private fun vibrateAndLaunchActivity(activityClass: Class<*>, finishAfter: Boolean = true) {
        vibrate()
        startActivity(Intent(this, activityClass))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        if (finishAfter) finish()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
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

    override fun onFavoritesFetched(favoriteQuotes: List<Quote>) {
        quoteList.clear()

        for (quote in favoriteQuotes) {
            Log.d("FavoriteQuote", "Quote ID: ${quote.quoteId}, Text: ${quote.text}, Ref: ${quote.reference}")
        }
        quoteList.addAll(favoriteQuotes)
        adapter.notifyDataSetChanged()

    }


}
