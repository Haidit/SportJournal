package com.example.sportjournal

import androidx.lifecycle.ViewModel
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference

class CreateRoutineViewModel : ViewModel() {
    val exerciseGroups = ArrayList<Pair<Exercise, ArrayList<Round>>>()

    fun saveRounds(routineReference: DatabaseReference){
        for (i in 0 until exerciseGroups.size) {
            for (j in 0 until exerciseGroups[i].second.size) {
                val dataMap = mutableMapOf<String, Any>()
                val exerciseGroup = exerciseGroups[i].first
                dataMap[EXERCISE_NAME] = exerciseGroup.exerciseName
                dataMap[EXERCISE_MUSCLE] = exerciseGroup.exerciseMuscleId
                dataMap[EXERCISE_TYPE_ID] = exerciseGroup.exerciseTypeId

                routineReference.child(NODE_EXERCISES)
                    .child(exerciseGroup.exerciseName).updateChildren(dataMap)

                routineReference.child(NODE_EXERCISES)
                    .child(exerciseGroup.exerciseName).child(
                        NODE_ROUNDS
                    ).child(exerciseGroups[i].second[j].roundId)
                    .setValue(exerciseGroups[i].second[j])
            }
        }
    }
}