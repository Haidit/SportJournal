package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.CreateRoutineViewModel
import com.example.sportjournal.R
import com.example.sportjournal.RoundAdapter
import com.example.sportjournal.RoundOnClickListener
import com.example.sportjournal.models.Round
import com.example.sportjournal.models.Routine
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference

class CreateRoutineFragment : BaseFragment(R.layout.fragment_create_routine) {

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mRoutineNameView: TextInputEditText
    private lateinit var mRoutineDayView: TextInputEditText
    private lateinit var mPlanId: String
    private lateinit var mPlanName: String
    private lateinit var mRoutineId: String
    private lateinit var planPath: DatabaseReference
    private lateinit var routineReference: DatabaseReference
    private lateinit var mWorkoutPerDayNumberView: TextInputEditText
    private lateinit var mFAB: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: CreateRoutineFragmentArgs by navArgs()
        mPlanId = args.planId
        mPlanName = args.planName
        mRoutineId = args.routineId

        val viewModel = ViewModelProvider(this).get(CreateRoutineViewModel::class.java)

        planPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(mPlanId)
        routineReference = planPath.child(NODE_ROUTINES).child(mRoutineId)

        mRoutineNameView = view.findViewById(R.id.newRoutineName)
        mRoutineDayView = view.findViewById(R.id.routineDay)
        mWorkoutPerDayNumberView = view.findViewById(R.id.workoutPerDay)
        mToolbar = view.findViewById(R.id.toolbar)

        routineReference.child(ROUTINE_NAME).addListenerForSingleValueEvent(AppValueEventListener {
            val routineName =
                if (it.value.toString() != "") it.value.toString() else getString(R.string.newRoutine)
            mRoutineNameView.setText(routineName)
            mToolbar.title = routineName
        })
        routineReference.child(ROUTINE_DAY).addListenerForSingleValueEvent(AppValueEventListener {
            mRoutineDayView.setText(it.value.toString())
        })
        routineReference.child(ROUTINE_PER_DAY_NUMBER)
            .addListenerForSingleValueEvent(AppValueEventListener {
                mWorkoutPerDayNumberView.setText(it.value.toString())
            })

        val adapter = RoundAdapter(requireContext(), viewModel.roundsPods, object : RoundOnClickListener {
            override fun onClicked(Round: Round) {
                showEditRoundDialog(Round, routineReference)
            }
        })
        val roundsRV = view.findViewById<RecyclerView>(R.id.exercise_list)
        roundsRV.layoutManager = LinearLayoutManager(context)
        roundsRV.adapter = adapter

        routineReference.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.roundsPods.clear()
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2.children.forEach {
                    val round = it.getValue(Round::class.java) ?: Round()
                    viewModel.roundsPods.add(round)
                }
            }
            adapter.notifyDataSetChanged()
        })

        mFAB = view.findViewById(R.id.add_button)
        mFAB.setOnClickListener {
            //showDialog(viewModel)
            adapter.notifyDataSetChanged()
        }

        mToolbar.apply {
            inflateMenu(R.menu.details_menu_bar)
            menu.apply {
                findItem(R.id.save_changes).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.save_changes))
                        setMessage(resources.getString(R.string.alert_check))
                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            if (validateForm(mRoutineNameView) && validateForm(mRoutineDayView) &&
                                validateForm(mWorkoutPerDayNumberView)
                            ) {
                                createRoutine()
                                val action =
                                    CreateRoutineFragmentDirections.actionCreateRoutineFragmentToPlanDetailsFragment(
                                        mPlanName,
                                        mPlanId
                                    )
                                findNavController().navigate(action)
                            }
                        }
                        setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                    true
                }
                findItem(R.id.delete).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.workout_delete_alert))
                        setMessage(resources.getString(R.string.alert_check))

                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            val action =
                                CreateRoutineFragmentDirections.actionCreateRoutineFragmentToPlanDetailsFragment(
                                    mPlanName,
                                    mPlanId
                                )
                            findNavController().navigate(action)
                            routineReference.removeValue()
                        }

                        setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                    true
                }
            }
        }
    }

//    private fun showDialog(viewModel: CreateRoutineViewModel) {
//        val dialogBinding = CreateRoundDialogBinding.inflate(layoutInflater)
//
//        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
//            .setView(dialogBinding.root)
//        val positiveButton = dialogBinding.addButton
//        val negativeButton = dialogBinding.cancelButton
//        val dialog = dialogBuilder.create()
//        dialog.show()
//        positiveButton.setOnClickListener {
//            if (validateForm(dialogBinding.exerciseNamePick)) {
//                val exerciseName = dialogBinding.exerciseNamePick.text.toString()
//                val exerciseWeight = if (dialogBinding.exerciseWeightPicker.text.toString() == "") 0
//                else dialogBinding.exerciseWeightPicker.text.toString().toInt()
//                val exerciseRepeats = if (dialogBinding.exerciseRepeats.text.toString() == "") 0
//                else dialogBinding.exerciseRepeats.text.toString().toInt()
//                val exerciseRest = if (dialogBinding.restTime.text.toString() == "") 0
//                else dialogBinding.restTime.text.toString().toInt()
//                val roundsView = dialogBinding.roundsNumber
//                val rounds = if (dialogBinding.roundsNumber.text.toString() == "") {
//                    1
//                } else roundsView.text.toString().toInt()
//                repeat(rounds) {
//                    val round = Round(
//                        exercise = Exercise(),
//                        roundId = routineReference.push().key.toString(),
//                        weight = exerciseWeight,
//                        reps = exerciseRepeats,
//                        restTime = exerciseRest
//                    )
//                    viewModel.roundsPods.add(round)
//                }
//                for (i in 0 until viewModel.roundsPods.size) {
//                    routineReference.child(NODE_ROUNDS).child(viewModel.roundsPods[i].roundId)
//                        .setValue(viewModel.roundsPods[i])
//                }
//                dialog.dismiss()
//            }
//        }
//        negativeButton.setOnClickListener { dialog.dismiss() }
//    }

    private fun createRoutine() {
        val routine = Routine(
            mRoutineNameView.text.toString(),
            mRoutineId,
            mWorkoutPerDayNumberView.text.toString().toInt(),
            mRoutineDayView.text.toString().toInt()
        )
        val dateMap = mutableMapOf<String, Any>()
        dateMap[ROUTINE_DAY] = routine.routineDay
        dateMap[ROUTINE_ID] = routine.routineId
        dateMap[ROUTINE_PER_DAY_NUMBER] = routine.routinePerDayNumber
        dateMap[ROUTINE_NAME] = routine.routineName
        routineReference.updateChildren(dateMap)
    }
}