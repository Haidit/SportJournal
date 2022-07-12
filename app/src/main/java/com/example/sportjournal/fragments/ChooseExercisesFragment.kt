package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.*
import com.example.sportjournal.databinding.ExerciseTypesPodsBinding
import com.example.sportjournal.databinding.FragmentChooseExercisesBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.utilits.AppValueEventListener
import com.example.sportjournal.utilits.NODE_EXERCISES
import com.example.sportjournal.utilits.REF_DATABASE_ROOT
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class ChooseExercisesFragment : Fragment(R.layout.fragment_choose_exercises) {

    private lateinit var exercisesPath: DatabaseReference
    private lateinit var binding: FragmentChooseExercisesBinding
    private lateinit var exerciseType: String
    private var exercises = ArrayList<Exercise>()
    private var exerciseGroups = ArrayList<Pair<String, ArrayList<Exercise>>>()
    private val viewModel: ChooseExercisesViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseExercisesBinding.bind(requireView())

        val mainAdapter = ExerciseTypeAdapter(viewModel.exerciseGroups, requireContext())
        binding.mainRV.layoutManager = LinearLayoutManager(context)
        binding.mainRV.adapter = mainAdapter

        exercisesPath = REF_DATABASE_ROOT.child(NODE_EXERCISES)
        exercisesPath.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            ds1.children.forEach { ds2 ->
                typeIdToTypeConvert(ds2)
                ds2.children.forEach {
                    val exercise = it.getValue(Exercise::class.java) ?: Exercise()
                    exercises.add(exercise)
                }
                    viewModel.exerciseGroups.add(Pair(exerciseType, exercises))
            }
            mainAdapter.notifyDataSetChanged()
        })
    }

    private fun typeIdToTypeConvert(ds: DataSnapshot) {
        exerciseType = when (ds.key.toString().toInt()) {
            0 -> getString(R.string.unknown)
            1 -> getString(R.string.firstType)
            2 -> getString(R.string.secondType)
            3 -> getString(R.string.thirdType)
            4 -> getString(R.string.fourthType)
            5 -> getString(R.string.fifthType)
            6 -> getString(R.string.sixthType)
            7 -> getString(R.string.seventhType)
            8 -> getString(R.string.eighthType)
            9 -> getString(R.string.ninthType)
            else -> getString(R.string.unknown)
        }
    }
}