package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.ExerciseType


class ChooseExercisesViewModel : ViewModel() {
    val exerciseGroups = ArrayList<ExerciseType>()
    val activeExercises = ArrayList<Exercise>()
}