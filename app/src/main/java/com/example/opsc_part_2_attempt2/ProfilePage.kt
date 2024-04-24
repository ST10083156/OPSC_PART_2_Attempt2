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
    //Page where user enters their details for storage in the "Users" collection in firestore
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
            //getting user id if they are signed in
            currentUserID = currentUser.uid
        }
        else{
            //if user not found then user will be returned to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.pictureBtn.setOnClickListener{pictureAdd()}

        //submit button creates user objects depending on the filled out fields and stores them...
        //...in firestore
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
            //once created this part adds them to the database using the "addToDatabase" function...
            //then it sends them to their TimesheetEntriesList
            if (user!=null) {
                addToDatabase(user)
                var intent = Intent(this, TimsheetEntriesList::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    //this function returns the uri of the image in the imageview by using the...
    //..."getImageUriFromBitmap" function to extract the bitmap of the image and converting it to a path
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

    //this function is used to add a picture from the gallery to the imageview
    fun pictureAdd(){

        var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)

    }

    //if the picture adds successfully, this function sets the imageview to the picture selected
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            binding.imageView.setImageURI(selectedImageUri)
        }
    }

    //this function adds the user to the database in the "Users" collection
    fun addToDatabase( user: User)
    {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("Users")

        val user = user
        usersCollection.add(user)
            .addOnSuccessListener {document ->

                Log.d(TAG, "User added ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }

    }
}