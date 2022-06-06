package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.databinding.ChoosePlanDialogBinding
import com.example.sportjournal.databinding.ChooseRoutineDialogBinding
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Routine
import com.example.sportjournal.models.Workout
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference

class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private lateinit var mWorkoutsListener: AppValueEventListener
    private lateinit var mRefWorkouts: DatabaseReference
    private lateinit var mPlansListener: AppValueEventListener
    private lateinit var mRefPlans: DatabaseReference
    private lateinit var mFAB: FloatingActionButton
    private lateinit var addButton1: Button
    private lateinit var addButton2: Button
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var planReference: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(WorkoutsViewModel::class.java)

        val adapter = WorkoutAdapter(viewModel.workoutPods, object : WorkoutOnClickListener {
            override fun onClicked(Workout: Workout) {
                val action =
                    WorkoutsFragmentDirections.actionWorkoutsFragmentToWorkoutDetailsFragment(
                        Workout.workoutId,
                        Workout.workoutName,
                        Workout.workoutDate,
                        Workout.workoutDifficulty.toString()
                    )
                findNavController().navigate(action)
            }
        })

        val workoutsRV = view.findViewById<RecyclerView>(R.id.workouts_list)
        workoutsRV.layoutManager = LinearLayoutManager(context)
        workoutsRV.adapter = adapter

        mRefWorkouts = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)
        mWorkoutsListener = AppValueEventListener { dataSnapshot ->
            viewModel.workoutPods.clear()
            dataSnapshot.children.forEach {
                val workout = it.getValue(Workout::class.java) ?: Workout()

                viewModel.workoutPods.add(0, workout)
            }
            adapter.notifyDataSetChanged()
        }
        mRefWorkouts.addValueEventListener(mWorkoutsListener)

        addButton1 = view.findViewById(R.id.add1)
        addButton1.setOnClickListener {
            findNavController().navigate(R.id.action_workoutsFragment_to_createWorkoutFragment)
        }

        addButton2 = view.findViewById(R.id.add2)
        addButton2.setOnClickListener {
            showChoosePlanDialog(viewModel)
        }

        mFAB = view.findViewById(R.id.add_button)
        mFAB.setOnClickListener {
            addButton1.isVisible = !addButton1.isVisible
            addButton2.isVisible = !addButton2.isVisible
        }
        mToolbar = view.findViewById(R.id.toolbar)
        mToolbar.apply {
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
        mRefWorkouts.removeEventListener(mWorkoutsListener)
    }

    private fun showChoosePlanDialog(viewModel: WorkoutsViewModel) {
        val dialogBinding = ChoosePlanDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        val adapter = PlanAdapter(viewModel.plansPods, object : PlanOnClickListener {
            override fun onClicked(Plan: Plan) {
                showChooseRoutineDialog(viewModel, Plan)
                dialog.dismiss()
            }
        })

        val plansRV = dialogBinding.plansList
        plansRV.layoutManager = LinearLayoutManager(context)
        plansRV.adapter = adapter

        mRefPlans = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS)
        mPlansListener = AppValueEventListener { dataSnapshot ->
            viewModel.plansPods.clear()
            dataSnapshot.children.forEach {
                val plan = it.getValue(Plan::class.java) ?: Plan()

                viewModel.plansPods.add(0, plan)
            }
            adapter.notifyDataSetChanged()
        }
        mRefPlans.addValueEventListener(mPlansListener)
    }

    private fun showChooseRoutineDialog(viewModel: WorkoutsViewModel, plan: Plan) {
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