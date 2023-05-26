package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.databinding.FragmentPlanDetailsBinding
import com.example.sportjournal.databinding.FragmentPlansBinding
import com.example.sportjournal.models.Routine
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference

class PlanDetailsFragment : Fragment(R.layout.fragment_plan_details) {

    private lateinit var binding: FragmentPlanDetailsBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var planName: String
    private lateinit var planId: String
    private lateinit var planReference: DatabaseReference
    private val viewModel: PlanDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlanDetailsBinding.bind(requireView())

        val args: PlanDetailsFragmentArgs by navArgs()
        planName = args.planName
        planId = args.planId

        planReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(planId)

        val adapter = RoutineAdapter(requireContext(), viewModel.routineList, object : RoutineOnClickListener {
            override fun onClicked(Routine: Routine) {
                val action =
                    PlanDetailsFragmentDirections.actionPlanDetailsFragmentToCreateRoutineFragment(
                        planId,
                        planName,
                        Routine.routineId
                    )
                findNavController().navigate(action)
            }
        })
        val routinesRV = binding.routinesList
        routinesRV.layoutManager = LinearLayoutManager(context)
        routinesRV.adapter = adapter

        planReference.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.routineList.clear()
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2.children.forEach {
                    val routine = it.getValue(Routine::class.java) ?: Routine()
                    viewModel.routineList.add(routine)
                }
            }
            adapter.notifyDataSetChanged()
        })

        toolbar = binding.toolbar
        toolbar.title = planName
        toolbar.apply {
            inflateMenu(R.menu.details_menu_bar)
            menu.apply {
                findItem(R.id.delete).setOnMenuItemClickListener {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle(resources.getString(R.string.plan_delete_alert))
                        setMessage(resources.getString(R.string.alert_check))
                        setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            findNavController().navigate(R.id.action_planDetailsFragment_to_plansFragment)
                            planReference.removeValue()
                        }
                        setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                    true
                }
                findItem(R.id.save_changes).isVisible = false
                true
            }
        }

        binding.addButton.setOnClickListener {
            val newRoutine =
                REF_DATABASE_ROOT
                    .child(NODE_USERS)
                    .child(UID)
                    .child(NODE_PLANS)
                    .child(planId)
                    .child(NODE_ROUTINES)
                    .push()
            val dateMap = mutableMapOf<String, Any>()
            dateMap[ROUTINE_ID] = newRoutine.key.toString()
            dateMap[ROUTINE_NAME] = ""
            dateMap[ROUTINE_DAY] = 0
            dateMap[ROUTINE_PER_DAY_NUMBER] = 0
            newRoutine.updateChildren(dateMap)
            val action =
                PlanDetailsFragmentDirections.actionPlanDetailsFragmentToCreateRoutineFragment(
                    planId,
                    planName,
                    newRoutine.key.toString()
                )
            findNavController().navigate(action)
        }
    }
}