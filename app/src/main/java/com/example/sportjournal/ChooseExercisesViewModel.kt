package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.ExerciseGroup


class ChooseExercisesViewModel : ViewModel() {
    var begin = 0L
    var end = 0L
    var count = 0
    val exerciseGroups = ArrayList<ExerciseGroup>()
    val activeExercises = ArrayList<Exercise>()

    fun clear() {
        begin = 0
        end = 0
        count = 0
    }
}