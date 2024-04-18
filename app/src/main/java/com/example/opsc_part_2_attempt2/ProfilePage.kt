package com.example.poe_part_2_attempt

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.opsc_part_2_attempt2.databinding.ActivityProfilePageBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePage : AppCompatActivity() {
    private val TAG: String = "ProfilePage"
    private lateinit var binding: ActivityProfilePageBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var  user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pictureBtn.setOnClickListener{pictureAdd()}
        binding.submitBtn.setOnClickListener{
            if (user!=null) {
                addToDatabase(user)
            }
        }
    }

    fun pictureAdd(){

        var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            binding.imageView.setImageURI(selectedImageUri)
        }
    }

    fun addToDatabase( user: User)
    {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("Users")

        val user = user
        usersCollection.add(user)
            .addOnSuccessListener {document ->

                Log.d(TAG, "User added with ID: ${document.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }

    }
}