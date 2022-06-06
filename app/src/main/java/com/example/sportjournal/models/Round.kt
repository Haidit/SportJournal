package com.example.sportjournal.models

data class Round(
    var roundId: String = "",
    var exerciseName: String = "",
    var weight: Int = 0,
    var reps: Int = 0,
    var restTime: Int = 0
)