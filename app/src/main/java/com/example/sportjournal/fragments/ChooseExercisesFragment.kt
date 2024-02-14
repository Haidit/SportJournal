package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.CHOOSE_EXERCISE_MODE
import com.example.sportjournal.ChooseExercisesViewModel
import com.example.sportjournal.ExerciseTypeAdapter
import com.example.sportjournal.R
import com.example.sportjournal.databinding.CreateNewExerciseDialogBinding
import com.example.sportjournal.databinding.FragmentChooseExercisesBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.ExerciseGroup
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference
import java.util.Date

class ChooseExercisesFragment : Fragment(R.layout.fragment_choose_exercises) {

    private lateinit var binding: FragmentChooseExercisesBinding
    private val viewModel: ChooseExercisesViewModel by activityViewModels()
    private lateinit var exercisesPath: DatabaseReference
    private val userExercisesPath =
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_EXERCISES)
    private lateinit var exerciseType: String
    private lateinit var exType: Array<String>
    private lateinit var mainAdapter: ExerciseTypeAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseExercisesBinding.bind(requireView())

        if (viewModel.begin == 0L) {
            viewModel.begin = Date().time
        }

        val args: ChooseExercisesFragmentArgs by navArgs()
        val isFromWorkouts = args.isFromWorkouts
        val planId = args.planId
        var planName = args.planName
        var routineId = args.routineId

        exType = resources.getStringArray(R.array.exercise_types_array)
        setAdapter()

        exercisesPath = REF_DATABASE_ROOT.child(NODE_EXERCISES)
        uploadData()

        binding.addButton.setOnClickListener {
            viewModel.end = Date().time
            mainAdapter.exerciseGroups.forEach { exType ->
                exType.exercisePair.second.forEach { ex ->
                    if (ex.active) {
                        viewModel.activeExercises.add(ex)
                        viewModel.count++
                    }
                }
            }
            var i = 0
            val actEx =
                Array(viewModel.activeExercises.size) { viewModel.activeExercises[i++] }
            val action = if (isFromWorkouts) {
                ChooseExercisesFragmentDirections.actionChooseExercisesFragmentToCreateWorkoutFragment(
                    exerciseList = actEx
                )
            } else {
                ChooseExercisesFragmentDirections.actionChooseExercisesFragmentToCreateRoutineFragment(
                    exerciseList = actEx,
                    planId = planId,
                    planName = planName,
                    routineId = routineId
                )
            }
            viewModel.activeExercises.clear()
            val sessionPath = REF_DATABASE_ROOT.child(NODE_TIMES).child(UID).child(
                NODE_SESSIONS
            ).push()
            sessionPath.child(NODE_AMOUNT).setValue(viewModel.count)
            sessionPath.child(NODE_TIME).setValue(viewModel.end - viewModel.begin)
            var mode = if (CHOOSE_EXERCISE_MODE) "text" else "pictures"
            sessionPath.child(NODE_MODE).setValue(mode)
            viewModel.clear()
            findNavController().navigate(action)
        }

        binding.createNewButton.setOnClickListener { showCreateNewExerciseDialog() }

    }

    private fun uploadData() {
        exercisesPath.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            viewModel.exerciseGroups.clear()
            ds1.children.forEach { ds2 ->
                exerciseType = typeIdToTypeConvert(ds2.key.toString().toInt())
                val exercises = ArrayList<Exercise>()
                ds2.children.forEach {
                    val exercise = it.getValue(Exercise::class.java) ?: Exercise()
                    exercises.add(exercise)
                }
                val exerciseGroup = ExerciseGroup(Pair(exerciseType, exercises))
                viewModel.exerciseGroups.add(exerciseGroup)
            }
            mainAdapter.notifyDataSetChanged()
        })
        userExercisesPath.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            ds1.children.forEach { ds2 ->
                val exercise = ds2.getValue(Exercise::class.java) ?: Exercise()
                viewModel.exerciseGroups.forEach {
                    if (it.exercisePair.first == typeIdToTypeConvert(
                            ds2.child(EXERCISE_TYPE_ID).value.toString().toInt()
                        )
                    )
                        it.exercisePair.second.add(exercise)
                }
            }
            mainAdapter.notifyDataSetChanged()
        })
    }

    private fun typeIdToTypeConvert(key: Int) = exType[key]

    private fun showCreateNewExerciseDialog() {
        val dialogBinding = CreateNewExerciseDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val spinner1 = dialogBinding.ExerciseTypeSpinner
        val exerciseName = dialogBinding.exerciseName
        val doneButton = dialogBinding.doneButton
        ArrayAdapter.createFromResource(
            requireContext(), R.array.exercise_types_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner1.adapter = adapter
        }
        val dialog = dialogBuilder.create()
        dialog.show()
        doneButton.setOnClickListener {
            if (validateForm(exerciseName)) {
                createNewExercise(exerciseName.text.toString(), spinner1.selectedItemPosition)
                uploadData()
                dialog.dismiss()
            }
        }
    }

    private fun createNewExercise(exerciseName: String, exerciseType: Int) {
        val dataMap = mutableMapOf<String, Any>()
        dataMap[EXERCISE_MUSCLE] = 0 // To do one day
        dataMap[EXERCISE_TYPE_ID] = exerciseType
        dataMap[EXERCISE_TYPE] = typeIdToTypeConvert(exerciseType)
        dataMap[EXERCISE_NAME] = exerciseName
        userExercisesPath.child(exerciseName).updateChildren(dataMap)
    }

    private fun setAdapter() {
        mainAdapter = ExerciseTypeAdapter(viewModel.exerciseGroups, requireContext())
        binding.mainRV.layoutManager = LinearLayoutManager(context)
        binding.mainRV.adapter = mainAdapter
    }

}