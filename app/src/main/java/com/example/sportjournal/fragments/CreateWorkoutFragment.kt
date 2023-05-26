package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.ExerciseSecondAdapter
import com.example.sportjournal.R
import com.example.sportjournal.WorkoutCreateViewModel
import com.example.sportjournal.databinding.FragmentCreateWorkoutBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round
import com.example.sportjournal.models.Weight
import com.example.sportjournal.models.Workout
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference

class CreateWorkoutFragment : BaseFragment(R.layout.fragment_create_workout) {

    private lateinit var binding: FragmentCreateWorkoutBinding
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var workoutDesc: TextInputEditText
    private lateinit var workoutsPath: DatabaseReference
    private lateinit var currentWorkoutPath: DatabaseReference
    private val viewModel: WorkoutCreateViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateWorkoutBinding.bind(requireView())

        val args: CreateWorkoutFragmentArgs by navArgs()

        workoutsPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)
        currentWorkoutPath = workoutsPath.push()

        val exercises = args.exerciseList
        if (exercises != null) {
            for (ex in exercises) {
                var check = true
                for (elem in viewModel.exerciseGroups) {
                    if (elem.first == ex) check = false
                }
                if (check) {
                    val exerciseGroup = Pair(ex, ArrayList<Round>())
                    viewModel.exerciseGroups.add(exerciseGroup)
                }
            }
        }

        val exerciseAdapter = ExerciseSecondAdapter(
            viewModel.exerciseGroups,
            requireContext(),
            layoutInflater
        )
        val exerciseRV = binding.exerciseList
        exerciseRV.layoutManager = LinearLayoutManager(context)
        exerciseRV.adapter = exerciseAdapter


        if (args.routineId != "0" || args.planId != "0") {
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(args.planId)
                .child(NODE_ROUTINES)
                .child(args.routineId).child(NODE_EXERCISES).addListenerForSingleValueEvent(
                    AppValueEventListener { ds1 ->
                        ds1.children.forEach { ds2 ->
                            val exercise = ds2.getValue(Exercise::class.java) ?: Exercise()
                            val roundsList = ArrayList<Round>()
                            ds2.child(NODE_ROUNDS).children.forEach {
                                val round = it.getValue(Round::class.java) ?: Round()
                                roundsList.add(round)
                            }
                            viewModel.exerciseGroups.add(Pair(exercise, roundsList))
                            exerciseAdapter.notifyDataSetChanged()
                        }
                    })
        }
        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_createWorkoutFragment_to_chooseExercisesFragment)
        }

        mToolbar = binding.toolbar

        mToolbar.apply {
            inflateMenu(R.menu.create_workout_menu_bar)
            menu.apply {
                findItem(R.id.save_workout).setOnMenuItemClickListener {
                    workoutDesc = binding.workoutDescInput
                    createWorkout()
                    val action =
                        CreateWorkoutFragmentDirections.actionCreateWorkoutFragmentToCreateWorkout2Fragment(
                            currentWorkoutPath.key.toString()
                        )
                    findNavController().navigate(action)
                    viewModel.roundsPods.clear()
                    viewModel.exerciseGroups.clear()
                    true
                }
            }
        }
    }

    private fun createWorkout() {

        val dataMap = mutableMapOf<String, Any>()

        val workout = Workout(
            "",
            "Без названия",
            resources.getString(R.string.date_unknown)
        )
        workout.weight = Weight(totalWeight = 0)

        dataMap[WORKOUT_ID] = currentWorkoutPath.key.toString()
        dataMap[WORKOUT_NAME] = workout.workoutName
        dataMap[WORKOUT_DATE] = workout.workoutDate
        dataMap[WORKOUT_DIFFICULTY] = workout.workoutDifficulty
        dataMap[WORKOUT_DESC] = workoutDesc.text.toString()

        currentWorkoutPath.updateChildren(dataMap)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showToast(task.exception?.message.toString())
                }
            }
        for (i in 0 until viewModel.exerciseGroups.size) {
            for (j in 0 until viewModel.exerciseGroups[i].second.size) {
                val dataMap = mutableMapOf<String, Any>()
                val exerciseGroup = viewModel.exerciseGroups[i].first
                val round = viewModel.exerciseGroups[i].second[j]
                when (exerciseGroup.exerciseTypeId) {
                    1 -> workout.weight.Group1Weight += round.weight * round.reps
                    2 -> workout.weight.Group2Weight += round.weight * round.reps
                    3 -> workout.weight.Group3Weight += round.weight * round.reps
                    4 -> workout.weight.Group4Weight += round.weight * round.reps
                    5 -> workout.weight.Group5Weight += round.weight * round.reps
                    6 -> workout.weight.Group6Weight += round.weight * round.reps
                    7 -> workout.weight.Group7Weight += round.weight * round.reps
                    8 -> workout.weight.Group8Weight += round.weight * round.reps
                    9 -> workout.weight.Group9Weight += round.weight * round.reps
                    0 -> workout.weight.Group0Weight += round.weight * round.reps
                }
                workout.weight.totalWeight += round.weight * round.reps

                dataMap[EXERCISE_NAME] = exerciseGroup.exerciseName
                dataMap[EXERCISE_MUSCLE] = exerciseGroup.exerciseMuscleId
                dataMap[EXERCISE_TYPE_ID] = exerciseGroup.exerciseTypeId

                currentWorkoutPath.child(NODE_EXERCISES)
                    .child(viewModel.exerciseGroups[i].first.exerciseName).updateChildren(dataMap)

                currentWorkoutPath.child(WEIGHT).setValue(workout.weight)
                currentWorkoutPath.child(NODE_EXERCISES)
                    .child(viewModel.exerciseGroups[i].first.exerciseName).child(
                        NODE_ROUNDS
                    ).child(viewModel.exerciseGroups[i].second[j].roundId)
                    .setValue(viewModel.exerciseGroups[i].second[j])
            }
        }
    }

}