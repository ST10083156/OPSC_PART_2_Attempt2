package com.example.opsc_part_2_attempt2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.opsc_part_2_attempt2.databinding.ActivityProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class ProfilePage : AppCompatActivity() {
    private val TAG: String = "ProfilePage"
    private lateinit var binding: ActivityProfilePageBinding
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var  user: User
    private lateinit var currentUserID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        binding.pictureBtn.setOnClickListener{pictureAdd()}
        binding.submitBtn.setOnClickListener{
if ( binding.usernameET.text != null)
{
    if(binding.imageView == null){
        if (currentUser != null) {
            user = User (
                userID = currentUserID,
                username = binding.usernameET.text.toString(),
                email = currentUser.email.toString(),
                pictureReference = null
            )
        }
    }
    else
    {
        if (currentUser != null) {
            user = User (
                userID = currentUserID,
                username = binding.usernameET.text.toString(),
                email = currentUser.email.toString(),
                pictureReference = getImageUri(binding.imageView)
            )
        }

    }
}

    else
        {
            Toast.makeText(this, "Please fill all the relevant fields", Toast.LENGTH_LONG)
        }
            if (user!=null) {
                addToDatabase(user)
                var intent = Intent(this, TimsheetEntriesList::class.java)
                startActivity(intent)
                finish()
            }
        }


    }
    private fun getImageUri(imageView: ImageView): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val uri = getImageUriFromBitmap(this, bitmap)
            return uri
        }
        return null
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
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