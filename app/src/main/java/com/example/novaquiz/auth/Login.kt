package com.example.novaquiz.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.novaquiz.R

import com.example.novaquiz.databinding.ActivityLoginBinding
import com.example.novaquiz.view.MainActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtforget.setOnClickListener {
            var mail=binding.etemail.text.toString()
            if(TextUtils.isEmpty(mail)){
                binding.etemail.error="Require Email"
                binding.etemail.requestFocus()
            }else{
                Forgetpass(mail)
            }

        }
        binding.txtsignup.setOnClickListener {
            startActivity(Intent(this,Signup::class.java))
            finish()
        }
        binding.btnsignin.setOnClickListener {
            var mail=binding.etemail.text.toString()
            var pass=binding.etpass.text.toString()

            if(TextUtils.isEmpty(mail)){
                binding.etemail.error="Require Email"
                binding.etemail.requestFocus()
            }else if(TextUtils.isEmpty(pass)){
                binding.etpass.error="Require Password"
                binding.etpass.requestFocus()
            }else if(!isInternetAvailable(this)){
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }

            else{
                binding.btnsignin.isEnabled = false
                binding.btnsignin.text = "Loading..."
                LoginAccount(mail,pass)

            }
        }

    }
    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetworkInfo
        return network != null && network.isConnected
    }

    fun LoginAccount(mail:String,pass:String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,pass).addOnCompleteListener { task->
            binding.btnsignin.isEnabled = true
            binding.btnsignin.text = "Sign In"
            if(task.isSuccessful){
                val shp=getSharedPreferences("Login", MODE_PRIVATE)
                val editor=shp.edit()
                editor.putBoolean("Login",true)
                editor.commit()
                Toast.makeText(this,"Login Successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                Toast.makeText(this,"Fail Login ",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun Forgetpass(mail:String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener { task->
            if(task.isSuccessful){
                startActivity(Intent(this,Forget::class.java))
            }else{
                Toast.makeText(this,"Reset Fail ",Toast.LENGTH_SHORT).show()
            }
        }
    }
}