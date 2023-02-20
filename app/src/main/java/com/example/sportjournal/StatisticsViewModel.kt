package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round

class StatisticsViewModel : ViewModel() {
    val exerciseGroups = ArrayList<Pair<Exercise, ArrayList<Round>>>()
    val statistics = ArrayList<Pair<String, Float>>()
    var totalWeight = 0f
}