package com.example.opsc_part_2_attempt2

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.Duration

data class TimesheetEntry(
    val UserID:String,
    val Name:String,
    var Date:Date,
    val Category:String,
    val Description:String,
    val TimeSpent: Long,
    val Image : Uri?,
)
