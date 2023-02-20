package com.example.sportjournal.models

data class Workout(
    var workoutId: String = "",
    var workoutName: String = "",
    var workoutDate: String = "",
    var workoutDifficulty: Int = 0,
    var weight: Weight = Weight()
)


data class Weight(
    var totalWeight: Int = 0,
    var Group1Weight: Int = 0,
    var Group2Weight: Int = 0,
    var Group3Weight: Int = 0,
    var Group4Weight: Int = 0,
    var Group5Weight: Int = 0,
    var Group6Weight: Int = 0,
    var Group7Weight: Int = 0,
    var Group8Weight: Int = 0,
    var Group9Weight: Int = 0,
    var Group0Weight: Int = 0
)