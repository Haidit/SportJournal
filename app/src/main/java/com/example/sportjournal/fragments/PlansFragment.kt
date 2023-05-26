package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.PlanAdapter
import com.example.sportjournal.PlanOnClickListener
import com.example.sportjournal.PlansViewModel
import com.example.sportjournal.R
import com.example.sportjournal.databinding.FragmentPlansBinding
import com.example.sportjournal.models.Plan
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference

class PlansFragment : Fragment(R.layout.fragment_plans) {

    private lateinit var binding: FragmentPlansBinding
    private lateinit var refPlans: DatabaseReference
    private val viewModel: PlansViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlansBinding.bind(requireView())

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

        val plansRV = binding.plansList
        plansRV.layoutManager = LinearLayoutManager(context)
        plansRV.adapter = adapter

        refPlans = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS)
        val plansListener = AppValueEventListener { dataSnapshot ->
            viewModel.plansPods.clear()
            dataSnapshot.children.forEach {
                val plan = it.getValue(Plan::class.java) ?: Plan()

                viewModel.plansPods.add(0, plan)
            }
            adapter.notifyDataSetChanged()
        }
        refPlans.addValueEventListener(plansListener)

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_plansFragment_to_createPlanDialogFragment)
        }
    }

}