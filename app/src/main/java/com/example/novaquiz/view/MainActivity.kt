package com.example.novaquiz.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.novaquiz.R
import com.example.novaquiz.adapter.QuoteAdapter
import com.example.novaquiz.data.Quote
import com.example.novaquiz.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private  val db=FirebaseFirestore.getInstance()
    private val quoteList = mutableListOf<Quote>()
   private lateinit var adapter:QuoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter=QuoteAdapter(quoteList)
        binding.rvQuote.layoutManager=LinearLayoutManager(this)
        binding.rvQuote.adapter=adapter

        binding.rvQuote.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        PagerSnapHelper().attachToRecyclerView(binding.rvQuote)
        showLoading(true)
        if (isInternetAvailable()) {
            fetchQuotes()
        } else {
            showLoading(false)
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
        }
        binding.btnTheme.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            startActivity(Intent(this,Theme::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        binding.btnGeneral.setOnClickListener {

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            startActivity(Intent(this,General::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
        binding.btnProfile.setOnClickListener {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(50)
            }
            startActivity(Intent(this,Profile::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

    }
    fun fetchQuotes(){
        db.collection("Quotes")
            .get()
            .addOnSuccessListener {result->
                quoteList.clear()
                for(document in result){
                    val quote=Quote(
                        text=document.getString("text")?:"",
                        reference = document.getString("reference")?:""
                    )
                    quoteList.add(quote)

                }
                adapter.notifyDataSetChanged()
                showLoading(false)
            }.addOnFailureListener { exception ->
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