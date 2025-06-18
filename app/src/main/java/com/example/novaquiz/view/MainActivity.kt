package com.example.novaquiz.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        fetchQuotes()
        binding.btnTheme.setOnClickListener {
            startActivity(Intent(this,Theme::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        binding.btnGeneral.setOnClickListener {
            startActivity(Intent(this,General::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
        binding.btnProfile.setOnClickListener {
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
            }
    }


}