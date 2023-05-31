package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.ExerciseSecondAdapter
import com.example.sportjournal.R
import com.example.sportjournal.WorkoutDetailsViewModel
import com.example.sportjournal.databinding.FragmentWorkoutDetailsBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference


class WorkoutDetailsFragment : BaseFragment(R.layout.fragment_workout_details) {

    private val viewModel: WorkoutDetailsViewModel by activityViewModels()
    private lateinit var binding: FragmentWorkoutDetailsBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var workoutId: String
    private lateinit var workoutName: String
    private lateinit var workoutDate: String
    private lateinit var workoutDifficulty: String
    private lateinit var workoutNameView: TextView
    private lateinit var workoutDateView: TextView
    private lateinit var workoutDifficultyView: TextView
    private lateinit var workoutDesk: TextInputEditText
    private lateinit var workoutDescText: String
    private lateinit var workoutReference: DatabaseReference
    private lateinit var exerciseAdapter: ExerciseSecondAdapter
    private var userID = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWorkoutDetailsBinding.bind(requireView())

        val args: WorkoutDetailsFragmentArgs by navArgs()

        userID = setId(args.userID)

        workoutId = args.workoutId
        workoutName = args.workoutName
        workoutDate = args.workoutDate
        workoutDifficulty = args.workoutDifficulty
        val totalWeight = args.totalWeight

        workoutNameView = binding.workoutName
        workoutDateView = binding.workoutDate
        workoutDifficultyView = binding.workoutDifficulty
        workoutDesk = binding.workoutDescInput

        workoutNameView.text = workoutName
        workoutDateView.text = workoutDate
        workoutDifficultyView.text =
            resources.getString(R.string.workout_difficulty, workoutDifficulty.toInt())

        workoutReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(userID).child(NODE_WORKOUTS).child(workoutId)

        workoutReference.child(
            WORKOUT_DESC
        ).addListenerForSingleValueEvent(AppValueEventListener {
            workoutDescText = it.value.toString()
            if (workoutDescText == "null") workoutDescText = ""
            workoutDesk.setText(workoutDescText)
        })

        exerciseAdapter = ExerciseSecondAdapter(
            viewModel.exerciseGroups,
            requireContext(),
            layoutInflater
        )
        val exerciseRV = binding.exerciseList
        exerciseRV.layoutManager = LinearLayoutManager(context)
        exerciseRV.adapter = exerciseAdapter
        setExerciseRVData()

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.apply {
            inflateMenu(R.menu.details_menu_bar)
            menu.apply {
                findItem(R.id.delete).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.workout_delete_alert))
                        setMessage(resources.getString(R.string.alert_check))

                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            findNavController().navigate(R.id.action_workoutDetailsFragment_to_workoutsFragment)
                            REF_DATABASE_ROOT.child(NODE_USERS).child(userID).child(NODE_WORKOUTS)
                                .child(workoutId).removeValue()
                        }

                        setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                    true
                }
                findItem(R.id.save_changes).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.save_changes))
                        setMessage(resources.getString(R.string.alert_check))

                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            saveChanges()
                            findNavController().navigate(R.id.action_workoutDetailsFragment_to_workoutsFragment)
                        }

                        setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                    true
                }
            }
        }
    }

    private fun saveChanges() {
        workoutReference.child(WORKOUT_DESC).setValue(workoutDesk.text.toString())
    }


    private fun setExerciseRVData() {
        viewModel.exerciseGroups.clear()
        workoutReference.child(NODE_EXERCISES)
            .addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
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
}