package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Routine
import com.example.sportjournal.models.Workout

class WorkoutsViewModel : ViewModel() {
    var workoutPods = ArrayList<Workout>()

    var plansPods = ArrayList<Plan>()

    var routinePods = ArrayList<Routine>()
}