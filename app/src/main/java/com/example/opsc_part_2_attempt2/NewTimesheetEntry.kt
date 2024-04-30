package com.example.opsc_part_2_attempt2

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_part_2_attempt2.databinding.ActivityNewTimesheetEntryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NewTimesheetEntry : AppCompatActivity() {

    private var timerRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private lateinit var buttonTimer: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewTimesheetEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        buttonTimer= binding.buttonTimer
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
            // Add functionality to add picture here
        }


    }

     fun addEntryToDatabase(newEntry: TimesheetEntry) {
        val db = FirebaseFirestore.getInstance()
        val timesheetCollection = db.collection("timesheetEntries")

        val tsEntry = newEntry
        timesheetCollection.add(tsEntry)
            .addOnSuccessListener {document ->

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
}

