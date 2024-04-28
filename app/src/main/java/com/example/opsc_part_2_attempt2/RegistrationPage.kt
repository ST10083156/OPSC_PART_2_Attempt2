package com.example.opsc_part_2_attempt2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.opsc_part_2_attempt2.databinding.ActivityRegistrationPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegistrationPage : AppCompatActivity() {
    private lateinit var binding : ActivityRegistrationPageBinding
    private lateinit var auth : FirebaseAuth

    //registration activity to create a new user auth in firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
binding.regBtn.setOnClickListener{register()}

        //redirects to login page should user already have an account
        binding.loginPageBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
startActivity(intent)
            finish()
        }
    }

    //register function to create new user in firebase
    private fun register(){

        var email = binding.emailET.text.toString()
        var password = binding.passwordET.text.toString()

        //attempts to create new user in firebase auth using email and password
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){

            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Singed Up", Toast.LENGTH_SHORT).show()
                finish()

                //if successful, redirects to the profile page...
                //...where they enter their details to be stored in firestore database
                val intent = Intent(this,ProfilePage::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }


        }
    }
}