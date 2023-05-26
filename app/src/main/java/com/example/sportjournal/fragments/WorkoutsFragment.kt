package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.*
import com.example.sportjournal.databinding.ChoosePlanDialogBinding
import com.example.sportjournal.databinding.ChooseRoutineDialogBinding
import com.example.sportjournal.databinding.FragmentWorkoutsBinding
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Routine
import com.example.sportjournal.models.Workout
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference

class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private lateinit var binding: FragmentWorkoutsBinding
    private val viewModel: WorkoutsViewModel by activityViewModels()
    private lateinit var workoutsListener: AppValueEventListener
    private lateinit var refWorkouts: DatabaseReference
    private lateinit var plansListener: AppValueEventListener
    private lateinit var refPlans: DatabaseReference
    private lateinit var addButton1: Button
    private lateinit var addButton2: Button
    private lateinit var planReference: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWorkoutsBinding.bind(requireView())

        val adapter = WorkoutAdapter(viewModel.workoutPods, object : WorkoutOnClickListener {
            override fun onClicked(Workout: Workout) {
                val action =
                    WorkoutsFragmentDirections.actionWorkoutsFragmentToWorkoutDetailsFragment(
                        Workout.workoutId,
                        Workout.workoutName,
                        Workout.workoutDate,
                        Workout.workoutDifficulty.toString(),
                        Workout.weight.totalWeight.toFloat()
                    )
                findNavController().navigate(action)
            }
        })

        val workoutsRV = binding.workoutsList
        workoutsRV.layoutManager = LinearLayoutManager(context)
        workoutsRV.adapter = adapter

        refWorkouts = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)
        workoutsListener = AppValueEventListener { ds ->
            viewModel.workoutPods.clear()
            ds.children.forEach {
                val workout = it.getValue(Workout::class.java) ?: Workout()

                viewModel.workoutPods.add(0, workout)
            }
            adapter.notifyDataSetChanged()
        }
        refWorkouts.addValueEventListener(workoutsListener)

        addButton1 = binding.add1
        addButton2 = binding.add2

        addButton1.setOnClickListener {
            findNavController().navigate(R.id.action_workoutsFragment_to_createWorkoutFragment)
        }
        addButton2.setOnClickListener {
            showChoosePlanDialog()
        }

        binding.addButton.setOnClickListener {
            addButton1.isVisible = !addButton1.isVisible
            addButton2.isVisible = !addButton2.isVisible
        }

        binding.toolbar.apply {
            inflateMenu(R.menu.main_menu_bar)
            menu.apply {
                findItem(R.id.log_out).setOnMenuItemClickListener {
                    AUTH.signOut()
                    requireActivity().finish()
                    true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        refWorkouts.removeEventListener(workoutsListener)
    }

    private fun showChoosePlanDialog() {
        val dialogBinding = ChoosePlanDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        val adapter = PlanAdapter(viewModel.plansPods, object : PlanOnClickListener {
            override fun onClicked(Plan: Plan) {
                showChooseRoutineDialog(Plan)
                dialog.dismiss()
            }
        })

        val plansRV = dialogBinding.plansList
        plansRV.layoutManager = LinearLayoutManager(context)
        plansRV.adapter = adapter

        refPlans = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS)
        plansListener = AppValueEventListener { dataSnapshot ->
            viewModel.plansPods.clear()
            dataSnapshot.children.forEach {
                val plan = it.getValue(Plan::class.java) ?: Plan()

                viewModel.plansPods.add(0, plan)
            }
            adapter.notifyDataSetChanged()
        }
        refPlans.addValueEventListener(plansListener)
    }

    private fun showChooseRoutineDialog(plan: Plan) {
        val dialogBinding = ChooseRoutineDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        planReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(plan.planId)

        val adapter = RoutineAdapter(
            requireContext(),
            viewModel.routinePods,
            object : RoutineOnClickListener {
                override fun onClicked(Routine: Routine) {
                    val action =
                        WorkoutsFragmentDirections.actionWorkoutsFragmentToCreateWorkoutFragment2(
                            planId = plan.planId,
                            routineId = Routine.routineId
                        )
                    findNavController().navigate(action)
                    dialog.dismiss()
                }
            })

        val routineRV = dialogBinding.routinesList
        routineRV.layoutManager = LinearLayoutManager(context)
        routineRV.adapter = adapter

        planReference.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.routinePods.clear()
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2.children.forEach {
                    val routine = it.getValue(Routine::class.java) ?: Routine()
                    viewModel.routinePods.add(routine)
                }
            }
            adapter.notifyDataSetChanged()
        })
    }
}