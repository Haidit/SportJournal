package com.example.sportjournal.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(
    var id: String = "",
    var email: String = "",
    var username: String = "",
    var sports: String = "None",
    var gender: String = "Not given"
)