import com.example.opsc_part_2_attempt2.R

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opsc_part_2_attempt2.R.id.buttonTimer
import java.util.*

class NewTimesheetEntry : AppCompatActivity() {

    private var timerRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_timesheet_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val buttonTimer: Button = findViewById(buttonTimer)
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

        val buttonAddPicture: Button = findViewById(R.id.buttonAddPicture)
        buttonAddPicture.setOnClickListener {
            // Add functionality to add picture here
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
        val buttonTimer: Button = findViewById(buttonTimer)
        if (timerRunning) {
            buttonTimer.text = "Stop Timer"
        } else {
            buttonTimer.text = "Start Timer"
        }
    }
}
