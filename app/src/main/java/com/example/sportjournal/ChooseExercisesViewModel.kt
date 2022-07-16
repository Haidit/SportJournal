package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise


class ChooseExercisesViewModel : ViewModel() {
    val exercises = ArrayList<Exercise>()
    val exerciseGroups = ArrayList<Pair<String, ArrayList<Exercise>>>()
}