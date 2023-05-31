package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Athlete
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Routine
import com.example.sportjournal.models.Workout

class SocialViewModel : ViewModel() {

    val requests = ArrayList<Athlete>()

    val athletes = ArrayList<Athlete>()

    val workouts = ArrayList<Workout>()

    val plans = ArrayList<Plan>()

    val routines = ArrayList<Routine>()
}