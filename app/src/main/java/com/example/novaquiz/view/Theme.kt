package com.example.novaquiz.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novaquiz.R
import com.example.novaquiz.adapter.ForyouAdapter

import com.example.novaquiz.adapter.MixesAdapter
import com.example.novaquiz.adapter.ThemeAdapter
import com.example.novaquiz.data.Themes
import com.example.novaquiz.databinding.ActivityThemeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class Theme : AppCompatActivity() {
    private lateinit var binding: ActivityThemeBinding
    private lateinit var themeAdapter: ThemeAdapter
    private lateinit var mixesAdapter:MixesAdapter
    private val db=FirebaseFirestore.getInstance()
    private val themelist= mutableListOf<Themes>()
    private lateinit var adapter: ForyouAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.rvgeneral.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        themeAdapter = ThemeAdapter()
        binding.rvgeneral.adapter = themeAdapter

        binding.rvmixes.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mixesAdapter= MixesAdapter()
        binding.rvmixes.adapter=mixesAdapter



       binding.rvforyou.layoutManager=GridLayoutManager(this,3)
       adapter=ForyouAdapter(themelist)
       binding.rvforyou.adapter=adapter

        binding.btnclose.setOnClickListener {
            navigateBackToMain()

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToMain()
            }
        })

       fectThemes()
    }
   fun fectThemes(){
       db.collection("themes")
           .get()
           .addOnSuccessListener {document->
               themelist.clear()
               for (doc in document){
                    val quote=doc.toObject(Themes::class.java)
                    themelist.add(quote)
               }
               adapter.notifyDataSetChanged()

           }
           .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
           }

   }
    private fun navigateBackToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
