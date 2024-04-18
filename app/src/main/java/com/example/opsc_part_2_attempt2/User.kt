package com.example.opsc_part_2_attempt2

import android.net.Uri

data class User(
    val userID : String,
    val  username : String,
    val email : String,
    val pictureReference : Uri?
)
