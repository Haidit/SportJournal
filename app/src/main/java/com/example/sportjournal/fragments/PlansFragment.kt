package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.*
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Workout
import com.example.sportjournal.utilits.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference

class PlansFragment : Fragment(R.layout.fragment_plans) {

    private lateinit var mPlansListener: AppValueEventListener
    private lateinit var mRefPlans: DatabaseReference
    private lateinit var mFAB: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(PlansViewModel::class.java)

        val adapter = PlanAdapter(viewModel.plansPods, object : PlanOnClickListener {
            override fun onClicked(Plan: Plan) {
                val action =
                    PlansFragmentDirections.actionPlansFragmentToPlanDetailsFragment(
                        Plan.planName,
                        Plan.planId
                    )
                findNavController().navigate(action)
            }
        })

        val plansRV = view.findViewById<RecyclerView>(R.id.plans_list)
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

        mFAB = view.findViewById(R.id.add_button)
        mFAB.setOnClickListener {
            findNavController().navigate(R.id.action_plansFragment_to_createPlanDialogFragment)
        }
    }
}