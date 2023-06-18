package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.sportjournal.R
import com.example.sportjournal.databinding.CreatePlanDialogBinding
import com.example.sportjournal.utilits.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DatabaseReference

class CreatePlanDialogFragment : BottomSheetDialogFragment() {

    private lateinit var planName: EditText
    private lateinit var plansPath: DatabaseReference
    private lateinit var currentPlanPath: DatabaseReference
    private lateinit var binding: CreatePlanDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_plan_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = CreatePlanDialogBinding.bind(requireView())

        plansPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS)

        binding.addButton.setOnClickListener {
            planName = binding.newPlanName
            createPlan(planName.text.toString())
            val action =
                CreatePlanDialogFragmentDirections.actionCreatePlanDialogFragmentToPlanDetailsFragment(
                    planName.text.toString(),
                    currentPlanPath.key.toString()
                )
            findNavController().navigate(action)
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_createPlanDialogFragment_to_plansFragment)
        }
    }

    private fun createPlan(planName: String) {
        currentPlanPath = plansPath.push()
        currentPlanPath.child(PLAN_ID).setValue(currentPlanPath.key.toString())
        currentPlanPath.child(PLAN_NAME).setValue(planName)
    }
}