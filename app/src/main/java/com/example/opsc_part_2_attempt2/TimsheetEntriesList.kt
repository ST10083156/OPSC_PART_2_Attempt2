package com.example.opsc_part_2_attempt2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_part_2_attempt2.databinding.ActivityTimsheetEntriesListBinding

//timesheet list that shows all the entries by the user in the firestore db
class TimsheetEntriesList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityTimsheetEntriesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //this part just makes sure that the content on the screen is offset...
        //...past any system displays like the task bar or anything else
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

    }


    }
