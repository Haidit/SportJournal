package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.databinding.FragmentCreateWorkoutBinding
import com.example.sportjournal.models.Round
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


        if (args.routineId != "0" && args.planId != "0") {
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(args.planId)
                .child(NODE_ROUTINES)
                .child(args.routineId).addValueEventListener(AppValueEventListener { dataSnapshot ->
                    viewModel.roundsPods.clear()
                    dataSnapshot.children.forEach { dataSnapshot2 ->
                        dataSnapshot2.children.forEach {
                            val round = it.getValue(Round::class.java) ?: Round()
                            viewModel.roundsPods.add(round)
                        }
                    }
                    exerciseAdapter.notifyDataSetChanged()
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

        val dateMap = mutableMapOf<String, Any>()

        val workout = Workout(
            "",
            "Без названия",
            "Дата не указана"
        )

        dateMap[WORKOUT_ID] = currentWorkoutPath.key.toString()
        dateMap[WORKOUT_NAME] = workout.workoutName
        dateMap[WORKOUT_DATE] = workout.workoutDate
        dateMap[WORKOUT_DIFFICULTY] = workout.workoutDifficulty
        dateMap[WORKOUT_DESC] = workoutDesc.text.toString()

        currentWorkoutPath.updateChildren(dateMap)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showToast(task.exception?.message.toString())
                }
            }
        for (i in 0 until viewModel.exerciseGroups.size) {
            for (j in 0 until viewModel.exerciseGroups[i].second.size) {
                currentWorkoutPath.child(NODE_EXERCISES)
                    .child(viewModel.exerciseGroups[i].first.exerciseName).child(
                        NODE_ROUNDS
                    ).child(viewModel.exerciseGroups[i].second[j].roundId)
                    .setValue(viewModel.exerciseGroups[i].second[j])
            }
        }
    }

}