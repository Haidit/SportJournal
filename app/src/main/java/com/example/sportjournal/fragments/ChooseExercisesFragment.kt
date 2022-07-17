package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.*
import com.example.sportjournal.databinding.FragmentChooseExercisesBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.ExerciseType
import com.example.sportjournal.utilits.AppValueEventListener
import com.example.sportjournal.utilits.NODE_EXERCISES
import com.example.sportjournal.utilits.REF_DATABASE_ROOT
import com.example.sportjournal.utilits.showToast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class ChooseExercisesFragment : Fragment(R.layout.fragment_choose_exercises) {

    private lateinit var exercisesPath: DatabaseReference
    private lateinit var binding: FragmentChooseExercisesBinding
    private lateinit var exerciseTypeGroup: String
    private val viewModel: ChooseExercisesViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseExercisesBinding.bind(requireView())

        val mainAdapter = ExerciseTypeAdapter(viewModel.exerciseGroups, requireContext())
        //val mainAdapter = ExerciseAdapter(viewModel.exercises, requireContext())
        binding.mainRV.layoutManager = LinearLayoutManager(context)
        binding.mainRV.adapter = mainAdapter

        exercisesPath = REF_DATABASE_ROOT.child(NODE_EXERCISES)
        exercisesPath.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            viewModel.exerciseGroups.clear()
            ds1.children.forEach { ds2 ->
                typeIdToTypeConvert(ds2)
                val exercises = ArrayList<Exercise>()
                ds2.children.forEach {
                    val exercise = it.getValue(Exercise::class.java) ?: Exercise()
                    exercises.add(exercise)
                }
                val exerciseType = ExerciseType(Pair(exerciseTypeGroup, exercises))
                viewModel.exerciseGroups.add(exerciseType)
            }
            mainAdapter.notifyDataSetChanged()
        })

        binding.addButton.setOnClickListener {
            mainAdapter.exerciseTypes.forEach { exType ->
                exType.exercisePair.second.forEach { ex ->
                    if (ex.active) {
                        viewModel.activeExercises.add(ex)}
                }
            }
            var i = 0
            val actEx: Array<Exercise> = Array(viewModel.activeExercises.size) { viewModel.activeExercises[i++] }
            val action =
                ChooseExercisesFragmentDirections.actionChooseExercisesFragmentToCreateWorkoutFragment(
                    exerciseList = actEx
                )
            findNavController().navigate(action)
        }
    }

    private fun typeIdToTypeConvert(ds: DataSnapshot) {
        exerciseTypeGroup = when (ds.key.toString().toInt()) {
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