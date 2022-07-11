package com.example.sportjournal.models

data class Workout(
    var workoutId: String = "",
    var workoutName: String = "",
    var workoutDate: String = "",
    var workoutDifficulty: Int = 0,
)