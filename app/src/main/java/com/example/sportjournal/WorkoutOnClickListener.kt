package com.example.sportjournal

import com.example.sportjournal.models.Workout

interface WorkoutOnClickListener {
    fun onClicked(Workout: Workout){
    }
}