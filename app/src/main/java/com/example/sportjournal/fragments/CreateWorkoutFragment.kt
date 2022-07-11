package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.R
import com.example.sportjournal.RoundAdapter
import com.example.sportjournal.RoundOnClickListener
import com.example.sportjournal.WorkoutCreateViewModel
import com.example.sportjournal.databinding.CreateRoundDialogBinding
import com.example.sportjournal.databinding.EditRoundDialogBinding
import com.example.sportjournal.models.Round
import com.example.sportjournal.models.Workout
import com.example.sportjournal.utilits.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference

class CreateWorkoutFragment : BaseFragment(R.layout.fragment_create_workout) {

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var workoutDesc: TextInputEditText
    private lateinit var workoutsPath: DatabaseReference
    private lateinit var currentWorkoutPath: DatabaseReference
    private val viewModel: WorkoutCreateViewModel by activityViewModels()
    private lateinit var adapter: RecyclerView.Adapter<RoundAdapter.RoundHolder>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: CreateWorkoutFragmentArgs by navArgs()

        workoutsPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)
        currentWorkoutPath = workoutsPath.push()
        adapter =
            RoundAdapter(requireContext(), viewModel.roundsPods, object : RoundOnClickListener {
                override fun onClicked(Round: Round) {
                    showEditRoundDialog(Round, viewModel.roundsPods)
                }
            })
        val roundsRV = view.findViewById<RecyclerView>(R.id.rounds_list)
        roundsRV.layoutManager = LinearLayoutManager(context)
        roundsRV.adapter = adapter

        if (args.routineId != "0" && args.planId != "0") {
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(args.planId)
                .child(NODE_ROUTINES)
                .child(args.routineId).addValueEventListener(AppValueEventListener { dataSnapshot ->
                    viewModel.roundsPods.clear()
                    dataSnapshot.children.forEach { dataSnapshot2 ->
                        dataSnapshot2.children.forEach {
                            val round = it.getValue(Round::class.java) ?: Round()
                            viewModel.roundsPods.add(round)
                        }
                    }
                    adapter.notifyDataSetChanged()
                })
        }

        view.findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            showDialog(viewModel)
            adapter.notifyDataSetChanged()
        }

        mToolbar = view.findViewById(R.id.toolbar)

        mToolbar.apply {
            inflateMenu(R.menu.create_workout_menu_bar)
            menu.apply {
                findItem(R.id.save_workout).setOnMenuItemClickListener {
                    workoutDesc = view.findViewById(R.id.workout_desc_input)
                    createWorkout(viewModel.roundsPods)
                    val action =
                        CreateWorkoutFragmentDirections.actionCreateWorkoutFragmentToCreateWorkout2Fragment(
                            currentWorkoutPath.key.toString()
                        )
                    findNavController().navigate(action)
                    viewModel.roundsPods.clear()
                    true
                }
            }
        }
    }

    private fun createWorkout(roundsPods: ArrayList<Round>) {

        val dateMap = mutableMapOf<String, Any>()

        val workout = Workout(
            "",
            "Без названия",
            "Дата не указана"
        )

        dateMap[WORKOUT_ID] = currentWorkoutPath.key.toString()
        dateMap[WORKOUT_NAME] = workout.workoutName
        dateMap[WORKOUT_DATE] = workout.workoutDate
        dateMap[WORKOUT_DIFFICULTY] = workout.workoutDifficulty
        dateMap[WORKOUT_DESC] = workoutDesc.text.toString()

        currentWorkoutPath.updateChildren(dateMap)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showToast(task.exception?.message.toString())
                }
            }
        for (i in 0 until roundsPods.size) {
            currentWorkoutPath.child(NODE_ROUNDS).child(roundsPods[i].roundId)
                .setValue(roundsPods[i])
        }
    }

    private fun showDialog(viewModel: WorkoutCreateViewModel) {
        val dialogBinding = CreateRoundDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val positiveButton = dialogBinding.addButton
        val negativeButton = dialogBinding.cancelButton
        val dialog = dialogBuilder.create()
        dialog.show()
        positiveButton.setOnClickListener {
            if (validateForm(dialogBinding.exerciseNamePick) && validateForm(dialogBinding.exerciseRepeats) &&
                validateForm(dialogBinding.exerciseWeightPicker) && validateForm(dialogBinding.restTime)
            ) {
                val exerciseName = dialogBinding.exerciseNamePick.text.toString()
                val exerciseWeight = dialogBinding.exerciseWeightPicker.text.toString().toInt()
                val exerciseRepeats = dialogBinding.exerciseRepeats.text.toString().toInt()
                val exerciseRest = dialogBinding.restTime.text.toString().toInt()
                val rounds =
                    if (dialogBinding.roundsNumber.text.toString() == "") 1
                    else dialogBinding.roundsNumber.text.toString().toInt()
                repeat(rounds) {
                    val round = Round(
                        roundId = currentWorkoutPath.push().key.toString(),
                        exerciseName = exerciseName,
                        weight = exerciseWeight,
                        reps = exerciseRepeats,
                        restTime = exerciseRest
                    )
                    viewModel.roundsPods.add(round)
                }
                dialog.dismiss()
            }
        }
        negativeButton.setOnClickListener { dialog.dismiss() }
    }

    private fun showEditRoundDialog(round: Round, rounds: ArrayList<Round>) {
        val dialogBinding = EditRoundDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val positiveButton = dialogBinding.saveButton
        val negativeButton = dialogBinding.deleteButton
        val exerciseNameView = dialogBinding.exerciseNamePick
        val weightView = dialogBinding.exerciseWeightPicker
        val repeatsView = dialogBinding.exerciseRepeats
        val restView = dialogBinding.restTime
        exerciseNameView.setText(round.exerciseName)
        weightView.setText(round.weight.toString())
        repeatsView.setText(round.reps.toString())
        restView.setText(round.restTime.toString())
        val dialog = dialogBuilder.create()
        dialog.show()
        positiveButton.setOnClickListener {
            round.exerciseName = exerciseNameView.text.toString()
            if (weightView.text.toString() == "") round.weight = 0
            else round.weight = weightView.text.toString().toInt()
            if (repeatsView.text.toString() == "") round.reps = 0
            else round.reps = repeatsView.text.toString().toInt()
            if (restView.text.toString() == "") round.restTime = 0
            else round.restTime = restView.text.toString().toInt()
            dialog.dismiss()
            adapter.notifyDataSetChanged()
        }
        negativeButton.setOnClickListener {
            rounds.remove(round)
            dialog.dismiss()
            adapter.notifyDataSetChanged()
        }
    }
}