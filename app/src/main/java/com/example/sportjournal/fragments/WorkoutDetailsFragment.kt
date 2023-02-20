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

    private lateinit var binding: FragmentWorkoutDetailsBinding
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mWorkoutId: String
    private lateinit var mWorkoutName: String
    private lateinit var mWorkoutDate: String
    private lateinit var mWorkoutDifficulty: String
    private lateinit var mWorkoutNameView: TextView
    private lateinit var mWorkoutDateView: TextView
    private lateinit var mWorkoutDifficultyView: TextView
    private lateinit var mWorkoutDesk: TextInputEditText
    private lateinit var workoutDescText: String
    private lateinit var workoutReference: DatabaseReference
    private lateinit var exerciseAdapter: ExerciseSecondAdapter
    private val viewModel: WorkoutDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWorkoutDetailsBinding.bind(requireView())

        val args: WorkoutDetailsFragmentArgs by navArgs()

        mWorkoutId = args.workoutId
        mWorkoutName = args.workoutName
        mWorkoutDate = args.workoutDate
        mWorkoutDifficulty = args.workoutDifficulty
        val totalWeight = args.totalWeight

        mWorkoutNameView = view.findViewById(R.id.workout_name)
        mWorkoutDateView = view.findViewById(R.id.workout_date)
        mWorkoutDifficultyView = view.findViewById(R.id.workout_difficulty)
        mWorkoutDesk = view.findViewById(R.id.workout_desc_input)

        mWorkoutNameView.text = mWorkoutName
        mWorkoutDateView.text = mWorkoutDate
        mWorkoutDifficultyView.text =
            resources.getString(R.string.workout_difficulty, mWorkoutDifficulty.toInt())

        workoutReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS).child(mWorkoutId)

        workoutReference.child(
            WORKOUT_DESC
        ).addListenerForSingleValueEvent(AppValueEventListener {
            workoutDescText = it.value.toString()
            if (workoutDescText == "null") workoutDescText = ""
            mWorkoutDesk.setText(workoutDescText)
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

        mToolbar = view.findViewById(R.id.toolbar)
        mToolbar.apply {
            inflateMenu(R.menu.details_menu_bar)
            menu.apply {
                findItem(R.id.delete).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.workout_delete_alert))
                        setMessage(resources.getString(R.string.alert_check))

                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            findNavController().navigate(R.id.action_workoutDetailsFragment_to_workoutsFragment)
                            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)
                                .child(mWorkoutId).removeValue()
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
        workoutReference.child(WORKOUT_DESC).setValue(mWorkoutDesk.text.toString())
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