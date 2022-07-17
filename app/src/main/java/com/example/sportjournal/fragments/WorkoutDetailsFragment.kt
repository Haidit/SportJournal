package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.models.Round
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference

class WorkoutDetailsFragment : BaseFragment(R.layout.fragment_workout_details) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: WorkoutDetailsFragmentArgs by navArgs()
        val viewModel = ViewModelProvider(this).get(WorkoutDetailsViewModel::class.java)

        mWorkoutId = args.workoutId
        mWorkoutName = args.workoutName
        mWorkoutDate = args.workoutDate
        mWorkoutDifficulty = args.workoutDifficulty

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

        val adapter =
            RoundAdapter(requireContext(), viewModel.roundsList, object : RoundOnClickListener {
                override fun onClicked(Round: Round) {
                    showEditRoundDialog(Round, workoutReference)
                }
            })

        val roundsRV = view.findViewById<RecyclerView>(R.id.exercise_list)
        roundsRV.layoutManager = LinearLayoutManager(context)
        roundsRV.adapter = adapter

        workoutReference.child(
            WORKOUT_DESC
        ).addListenerForSingleValueEvent(AppValueEventListener {
            workoutDescText = it.value.toString()
            if (workoutDescText == "null") workoutDescText = ""
            mWorkoutDesk.setText(workoutDescText)
        })

        workoutReference.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.roundsList.clear()
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2.children.forEach {
                    val round = it.getValue(Round::class.java) ?: Round()
                    viewModel.roundsList.add(round)
                }
            }
            adapter.notifyDataSetChanged()
        })

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
                            workoutReference.child(WORKOUT_DESC)
                                .setValue(mWorkoutDesk.text.toString())
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
}