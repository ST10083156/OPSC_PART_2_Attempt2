package com.example.opsc_part_2_attempt2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.opsc_part_2_attempt2.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.regPageBtn.setOnClickListener{var intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
            finish()}
        binding.loginBtn.setOnClickListener{login()}


    }
    private fun login(){
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful)
            {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val user = FirebaseAuth.getInstance().currentUser
                if (user!=null)
                {
                    userID = user.uid
                }
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document("UserID").get().addOnSuccessListener {
                        document -> if (document.exists())
                {
                    val existingID = document.getString("UserID")
                    val checkUserID = userID
                    if (existingID == checkUserID)
                    {
                      /*  val intent = Intent (this, ProfileViewPage::class.java)
                        startActivity(intent)
                        finish()*/
                    }
                }
                else {
                    Toast.makeText(this,"New user, please enter your details", Toast.LENGTH_SHORT)
                    val intent = Intent (this, ProfilePage::class.java)
                    startActivity(intent)
                    finish()
                }
                }
            }
            else
            {
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}