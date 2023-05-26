package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.CreateRoutineViewModel
import com.example.sportjournal.ExerciseSecondAdapter
import com.example.sportjournal.R
import com.example.sportjournal.databinding.FragmentCreateRoutineBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round
import com.example.sportjournal.models.Routine
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference

class CreateRoutineFragment : BaseFragment(R.layout.fragment_create_routine) {

    private lateinit var binding: FragmentCreateRoutineBinding
    private val viewModel: CreateRoutineViewModel by activityViewModels()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var routineNameView: TextInputEditText
    private lateinit var routineDayView: TextInputEditText
    private lateinit var planId: String
    private lateinit var planName: String
    private lateinit var routineId: String
    private lateinit var planPath: DatabaseReference
    private lateinit var routineReference: DatabaseReference
    private lateinit var workoutPerDayNumberView: TextInputEditText
    private lateinit var adapter: ExerciseSecondAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: CreateRoutineFragmentArgs by navArgs()
        val exercises = args.exerciseList
        planId = args.planId
        planName = args.planName
        routineId = args.routineId

        binding = FragmentCreateRoutineBinding.bind(requireView())

        planPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(planId)
        routineReference = planPath.child(NODE_ROUTINES).child(routineId)

        routineNameView = binding.newRoutineName
        routineDayView = binding.routineDay
        workoutPerDayNumberView = binding.workoutPerDay
        toolbar = binding.toolbar

        routineReference.child(ROUTINE_NAME).addListenerForSingleValueEvent(AppValueEventListener {
            val routineName =
                if (it.value.toString() != "") it.value.toString() else getString(R.string.newRoutine)
            routineNameView.setText(routineName)
            toolbar.title = routineName
        })
        routineReference.child(ROUTINE_DAY).addListenerForSingleValueEvent(AppValueEventListener {
            routineDayView.setText(it.value.toString())
        })
        routineReference.child(ROUTINE_PER_DAY_NUMBER)
            .addListenerForSingleValueEvent(AppValueEventListener {
                workoutPerDayNumberView.setText(it.value.toString())
            })

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

        adapter = ExerciseSecondAdapter(
            viewModel.exerciseGroups,
            requireContext(),
            layoutInflater
        )
        val exerciseRV = binding.exerciseList
        exerciseRV.layoutManager = LinearLayoutManager(context)
        exerciseRV.adapter = adapter
        setExerciseRVData()

        binding.addButton.setOnClickListener {
            val action =
                CreateRoutineFragmentDirections.actionCreateRoutineFragmentToChooseExercisesFragment(
                    planId = planId, planName = planName, routineId = routineId
                )
            findNavController().navigate(action)
            adapter.notifyDataSetChanged()
        }

        toolbar.apply {
            inflateMenu(R.menu.details_menu_bar)
            menu.apply {
                findItem(R.id.save_changes).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.save_changes))
                        setMessage(resources.getString(R.string.alert_check))
                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            if (validateForm(routineNameView) && validateForm(routineDayView) && validateForm(
                                    workoutPerDayNumberView
                                )
                            ) {
                                updateRoutine()
                                val action =
                                    CreateRoutineFragmentDirections.actionCreateRoutineFragmentToPlanDetailsFragment(
                                        planName = planName,
                                        planId = planId
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
                                    planName, planId
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

    override fun onStop() {
        super.onStop()
        viewModel.exerciseGroups.clear()
    }

    private fun updateRoutine() {
        val routine = Routine(
            routineNameView.text.toString(),
            routineId,
            workoutPerDayNumberView.text.toString().toInt(),
            routineDayView.text.toString().toInt()
        )
        val dataMap = mutableMapOf<String, Any>()
        dataMap[ROUTINE_DAY] = routine.routineDay
        dataMap[ROUTINE_ID] = routine.routineId
        dataMap[ROUTINE_PER_DAY_NUMBER] = routine.routinePerDayNumber
        dataMap[ROUTINE_NAME] = routine.routineName
        routineReference.updateChildren(dataMap)

        viewModel.saveRounds(routineReference)
        viewModel.exerciseGroups.clear()
    }

    private fun setExerciseRVData() {
        routineReference.child(NODE_EXERCISES)
            .addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
                ds1.children.forEach { ds2 ->
                    val exercise = ds2.getValue(Exercise::class.java) ?: Exercise()
                    val roundsList = ArrayList<Round>()
                    ds2.child(NODE_ROUNDS).children.forEach {
                        val round = it.getValue(Round::class.java) ?: Round()
                        roundsList.add(round)
                    }
                    viewModel.exerciseGroups.add(Pair(exercise, roundsList))
                    adapter.notifyDataSetChanged()
                }
            })
    }

}