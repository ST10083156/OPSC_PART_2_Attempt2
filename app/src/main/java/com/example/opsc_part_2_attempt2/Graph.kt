package com.fake.graphed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_part_2_attempt2.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Graph : AppCompatActivity() {
    private lateinit var lineChart: LineChart
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph)

        lineChart = findViewById(R.id.lineChart)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        // Assuming you have the user's ID stored in a variable called "userId"
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Query Firestore to fetch timespent data for the logged-in user
        userId?.let { uid ->
            FirebaseFirestore.getInstance().collection("timesheetEntries")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val timespentByDay = mutableMapOf<String, Float>()

                    snapshot.documents.forEach { document ->
                        val date = document.getString("date") ?: ""
                        val timespent = document.getString("timespent")?.toFloat() ?: 0f

                        // Aggregate total timespent for each day
                        val totalTimespent = timespentByDay[date] ?: 0f
                        timespentByDay[date] = totalTimespent + timespent
                    }

                    // Convert aggregated data to chart entries
                    val entries = timespentByDay.entries.mapIndexed { index, (date, totalTimespent) ->
                        Entry(index.toFloat(), totalTimespent)
                    }

                    // Create a LineDataSet using the chart entries
                    val dataSet = LineDataSet(entries, "Total Timespent")

                    // Populate the LineChart with the data set
                    lineChart.data = LineData(dataSet)

                    // Refresh the chart to display the updated data
                    lineChart.invalidate()
                }
        }
    }
}
