package com.example.novaquiz.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.novaquiz.R
import com.example.novaquiz.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            var mail=binding.reemail.text.toString()
            var pass=binding.repass.text.toString()
            var name=binding.rename.text.toString()

            if(TextUtils.isEmpty(mail)){
               binding.reemail.error="Email Require"
                binding.reemail.requestFocus()
            }else if(TextUtils.isEmpty(name)){
                binding.rename.error="Name Require"
                binding.rename.requestFocus()
            }
            else if(TextUtils.isEmpty(pass)){
                binding.repass.error="Require Password"
                binding.repass.requestFocus()
            }else if(!isInternetAvailable(this)){
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }

            else{
                binding.btnSignup.isEnabled = false
                binding.btnSignup.text = "Signing Up..."
                registerAccount(mail,name,pass)

            }
        }
        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }
        }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun registerAccount( email: String, name: String,pass: String) {
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        Log.d("RegisterDebug", "Trying to register with: $email")

        auth.createUserWithEmailAndPassword(email.trim(), pass).addOnCompleteListener { task ->

            binding.btnSignup.isEnabled = true
            binding.btnSignup.text = "Sign Up"
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val userMap = hashMapOf(
                    "name" to name,
                    "email" to email.trim(),
                    "themeId" to 0
                )

                if (userId != null) {
                    db.collection("Users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Register Successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,Login::class.java))
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "User added but Firestore failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Register failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}
