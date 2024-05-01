package com.example.opsc_part_2_attempt2

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_part_2_attempt2.databinding.ActivityNewTimesheetEntryBinding
import com.example.opsc_part_2_attempt2.databinding.ActivityProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class NewTimesheetEntry : AppCompatActivity() {

    private var timerRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private lateinit var buttonTimer: Button
    private lateinit var timesheetEntry: TimesheetEntry
    private lateinit var currentUserID: String
    private lateinit var binding: ActivityNewTimesheetEntryBinding

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTimesheetEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            //getting user id if they are signed in
            currentUserID = currentUser.uid
        }


        buttonTimer = binding.buttonTimer
        buttonTimer.setOnClickListener {
            if (timerRunning) {
                // Stop the timer
                elapsedTime += System.currentTimeMillis() - startTime
                timerRunning = false
                buttonTimer.text = "Start Timer"
            } else {
                // Start the timer
                startTime = System.currentTimeMillis()
                timerRunning = true
                buttonTimer.text = "Stop Timer"
            }
        }
        val buttonAddPicture: Button = binding.buttonAddPicture
        buttonAddPicture.setOnClickListener {
            pictureAdd()
        }
        binding.doneBtn.setOnClickListener {
            if (binding.editTextName.text == null ||
                binding.editTextCategory.text == null ||
                binding.editTextDescription.text == null ||
                binding.editTextDate.text == null &&
                binding.imageView.drawable == R.drawable.ic_launcher_foreground.toDrawable()
            ) {
                timesheetEntry = TimesheetEntry(
                    UserID = currentUserID,
                    Name = binding.editTextName.text.toString(),
                    Category = binding.editTextCategory.text.toString(),
                    Date = convertTextToDate(binding.editTextDate.text.toString()),
                    Description = binding.editTextDescription.text.toString(),
                    TimeSpent = elapsedTime,
                    Image = null
                )
                addEntryToDatabase(timesheetEntry)

            }
            else{
                timesheetEntry = TimesheetEntry(
                    UserID = currentUserID,
                    Name = binding.editTextName.text.toString(),
                    Category = binding.editTextCategory.text.toString(),
                    Date = convertTextToDate(binding.editTextDate.text.toString()),
                    Description = binding.editTextDescription.text.toString(),
                    TimeSpent = elapsedTime,
                    Image = getImageUri(binding.imageView )
                )
                addEntryToDatabase(timesheetEntry)
            }

        }
    }


    fun addEntryToDatabase(newEntry: TimesheetEntry) {
        val db = FirebaseFirestore.getInstance()
        val timesheetCollection = db.collection("timesheetEntries")

        val tsEntry = newEntry
        timesheetCollection.add(tsEntry)
            .addOnSuccessListener { document ->

                Log.d(TAG, "User added ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("timerRunning", timerRunning)
        outState.putLong("startTime", startTime)
        outState.putLong("elapsedTime", elapsedTime)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        timerRunning = savedInstanceState.getBoolean("timerRunning")
        startTime = savedInstanceState.getLong("startTime")
        elapsedTime = savedInstanceState.getLong("elapsedTime")
        // Update UI based on saved state

        if (timerRunning) {
            buttonTimer.text = "Stop Timer"
        } else {
            buttonTimer.text = "Start Timer"
        }
    }

    fun convertTextToDate(inputText: String): Date {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(inputText)
            return date
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

    //this function is used to add a picture from the gallery to the imageview
    fun pictureAdd(){

        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

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
}



