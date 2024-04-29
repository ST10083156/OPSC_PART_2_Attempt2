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

    //login activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //on button click to redirect to registration page
        binding.regPageBtn.setOnClickListener{var intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
            finish()}
        binding.loginBtn.setOnClickListener{login()}


    }

    //login function with firebase authentication
    private fun login(){
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()

        //attempts to sign user in with email and password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful)
            {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()

                //gets an instance of the current user
                val user = FirebaseAuth.getInstance().currentUser
                if (user!=null)
                {
                    //if user is found, user id is extracted
                    userID = user.uid
                }
                else
                {
                    Toast.makeText(this,"User not found", Toast.LENGTH_SHORT)
                }

                //checks whether user details have been stored in firebase by checking if the user id is stored
                val db = FirebaseFirestore.getInstance()
                db.collection("Users").whereEqualTo("userID",userID).get().addOnSuccessListener {
                        documents -> if (!documents.isEmpty)
                {
                        db.collection("timesheetEntries").whereEqualTo("userID", userID).get().addOnSuccessListener {
                            timesheetDocuments -> if (!timesheetDocuments.isEmpty)
                            {
                                val intent = Intent (this, TimsheetEntriesList::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                val intent = Intent (this, NewTimesheetEntry::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
                //if it is not stored it redirects to a page where they can enter their details
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