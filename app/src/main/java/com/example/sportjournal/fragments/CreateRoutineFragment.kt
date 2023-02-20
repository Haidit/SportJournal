package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.databinding.CreateRoundDialogBinding
import com.example.sportjournal.databinding.FragmentCreateRoutineBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round
import com.example.sportjournal.models.Routine
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fab: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: CreateRoutineFragmentArgs by navArgs()
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

        val adapter =
            RoundAdapter(requireContext(), viewModel.roundsPods, object : RoundOnClickListener {
                override fun onClicked(Round: Round) {
                    showEditRoundDialog(Round, routineReference)
                }
            })
        val roundsRV = binding.exerciseList
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

        fab = binding.addButton
        fab.setOnClickListener {
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
                            if (validateForm(routineNameView) && validateForm(routineDayView) &&
                                validateForm(workoutPerDayNumberView)
                            ) {
                                createRoutine()
                                val action =
                                    CreateRoutineFragmentDirections.actionCreateRoutineFragmentToPlanDetailsFragment(
                                        planName,
                                        planId
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
                                    planName,
                                    planId
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

    private fun createRoutine() {
        val routine = Routine(
            routineNameView.text.toString(),
            routineId,
            workoutPerDayNumberView.text.toString().toInt(),
            routineDayView.text.toString().toInt()
        )
        val dateMap = mutableMapOf<String, Any>()
        dateMap[ROUTINE_DAY] = routine.routineDay
        dateMap[ROUTINE_ID] = routine.routineId
        dateMap[ROUTINE_PER_DAY_NUMBER] = routine.routinePerDayNumber
        dateMap[ROUTINE_NAME] = routine.routineName
        routineReference.updateChildren(dateMap)
    }
}