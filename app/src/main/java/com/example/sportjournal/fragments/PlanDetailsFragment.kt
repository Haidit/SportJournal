package com.example.sportjournal.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.PlanDetailsViewModel
import com.example.sportjournal.R
import com.example.sportjournal.RoutineAdapter
import com.example.sportjournal.RoutineOnClickListener
import com.example.sportjournal.models.Routine
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference

class PlanDetailsFragment : Fragment(R.layout.fragment_plan_details) {

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mFAB: FloatingActionButton
    private lateinit var mPlanName: String
    private lateinit var mPlanId: String
    private lateinit var planReference: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PlanDetailsFragmentArgs by navArgs()
        mPlanName = args.planName
        mPlanId = args.planId

        val viewModel = ViewModelProvider(this).get(PlanDetailsViewModel::class.java)
        planReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(mPlanId)

        val adapter = RoutineAdapter(requireContext(), viewModel.routineList, object : RoutineOnClickListener {
            override fun onClicked(Routine: Routine) {
                val action =
                    PlanDetailsFragmentDirections.actionPlanDetailsFragmentToCreateRoutineFragment(
                        mPlanId,
                        mPlanName,
                        Routine.routineId
                    )
                findNavController().navigate(action)
            }
        })
        val routinesRV = view.findViewById<RecyclerView>(R.id.routines_list)
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

        mToolbar = view.findViewById(R.id.toolbar)
        mToolbar.title = mPlanName
        mToolbar.apply {
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
        mFAB = view.findViewById(R.id.add_button)
        mFAB.setOnClickListener {
            val newRoutine =
                REF_DATABASE_ROOT
                    .child(NODE_USERS)
                    .child(UID)
                    .child(NODE_PLANS)
                    .child(mPlanId)
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
                    mPlanId,
                    mPlanName,
                    newRoutine.key.toString()
                )
            findNavController().navigate(action)
        }
    }
}