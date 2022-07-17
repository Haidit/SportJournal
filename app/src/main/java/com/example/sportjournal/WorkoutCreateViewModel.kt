package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round

class WorkoutCreateViewModel : ViewModel() {
    val roundsPods = ArrayList<Round>()
    val exerciseGroups = ArrayList<Pair<Exercise, ArrayList<Round>>>()
}