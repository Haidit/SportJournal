package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.ExerciseGroup


class ChooseExercisesViewModel : ViewModel() {
    val exerciseGroups = ArrayList<ExerciseGroup>()
    val activeExercises = ArrayList<Exercise>()
}