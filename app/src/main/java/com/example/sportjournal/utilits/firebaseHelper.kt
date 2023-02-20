package com.example.sportjournal.utilits

import com.example.sportjournal.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

lateinit var AUTH: FirebaseAuth
lateinit var DATABASE: FirebaseDatabase
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var UID: String
var USER: User = User()

const val NODE_USERS = "users"
const val USERNAME = "username"
const val USER_SPORTS = "sports"
const val USER_GENDER = "gender"

const val NODE_WORKOUTS = "workouts"
const val WORKOUT_ID = "workoutId"
const val WORKOUT_NAME = "workoutName"
const val WORKOUT_DATE = "workoutDate"
const val WORKOUT_DIFFICULTY = "workoutDifficulty"
const val NODE_ROUNDS = "rounds"
const val WORKOUT_DESC = "workoutDesc"

const val NODE_PLANS = "plans"
const val PLAN_ID = "planId"
const val PLAN_NAME = "planName"

const val NODE_ROUTINES = "routines"
const val ROUTINE_ID = "routineId"
const val ROUTINE_DAY = "routineDay"
const val ROUTINE_PER_DAY_NUMBER = "routinePerDayNumber"
const val ROUTINE_NAME = "routineName"

const val NODE_EXERCISES = "exercises"
const val EXERCISE_NAME = "exerciseName"
const val EXERCISE_MUSCLE = "exerciseMuscle"
const val EXERCISE_TYPE_ID = "exerciseTypeId"
const val EXERCISE_TYPE = "exerciseType"
const val WEIGHT = "weight"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    DATABASE =
        Firebase.database("https://sport-journal-d4b7d-default-rtdb.europe-west1.firebasedatabase.app")
    REF_DATABASE_ROOT = DATABASE.reference
    UID = AUTH.currentUser?.uid.toString()
}

fun initUser() {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(User::class.java) ?: User()
        })
}