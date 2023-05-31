package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.*
import com.example.sportjournal.databinding.ChooseItemDialogBinding
import com.example.sportjournal.databinding.CoachsActionsDialogBinding
import com.example.sportjournal.databinding.FragmentSocialBinding
import com.example.sportjournal.databinding.SendRequestDialogBinding
import com.example.sportjournal.models.*
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference

class SocialFragment : Fragment(R.layout.fragment_social) {

    private lateinit var binding: FragmentSocialBinding
    private lateinit var requestsAdapter: RequestsAdapter
    private lateinit var athletesAdapter: AthletesAdapter
    private lateinit var requestsPath: DatabaseReference
    private lateinit var athletesPath: DatabaseReference
    private lateinit var workoutsPath: DatabaseReference
    private lateinit var plansPath: DatabaseReference
    private lateinit var routinesPath: DatabaseReference
    private lateinit var requestsListener: AppValueEventListener
    private lateinit var athletesListener: AppValueEventListener
    private lateinit var plansListener: AppValueEventListener
    private val viewModel: SocialViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSocialBinding.bind(requireView())

        binding.fab.setOnClickListener {
            showSendRequestDialog()
            requestsAdapter.notifyDataSetChanged()
        }

        requestsPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_REQUESTS)
        athletesPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_ATHLETES)

        requestsAdapter = RequestsAdapter(viewModel.requests, object : AthleteOnClickListener {
            override fun onAcceptButtonClicked(request: Athlete) {
                with(athletesPath.child(request.id)) {
                    child(USERNAME).setValue(request.username)
                    child("id").setValue(request.id)
                    child(USER_EMAIL).setValue(request.email)
                }
                requestsPath.child(request.id).removeValue()
            }

            override fun onRejectButtonClicked(request: Athlete) {
                requestsPath.child(request.id).removeValue()
            }
        })

        val requestsRV = binding.requestsRV
        requestsRV.layoutManager = LinearLayoutManager(context)
        requestsRV.adapter = requestsAdapter

        athletesAdapter = AthletesAdapter(viewModel.athletes, object : AthleteOnClickListener {
            override fun onClicked(athlete: Athlete) {
                showCoachActionsDialog(athlete.id)
            }
        })

        val athletesRV = binding.athletesRV
        athletesRV.layoutManager = LinearLayoutManager(context)
        athletesRV.adapter = athletesAdapter

        requestsListener = AppValueEventListener { ds1 ->
            viewModel.requests.clear()
            ds1.children.forEach {
                val request = it.getValue(Athlete::class.java) ?: Athlete()
                viewModel.requests.add(request)
            }
            requestsAdapter.notifyDataSetChanged()
        }
        athletesListener = AppValueEventListener { ds1 ->
            viewModel.athletes.clear()
            ds1.children.forEach {
                val athlete = it.getValue(Athlete::class.java) ?: Athlete()
                viewModel.athletes.add(athlete)
            }
            athletesAdapter.notifyDataSetChanged()
        }

        requestsPath.addValueEventListener(requestsListener)
        athletesPath.addValueEventListener(athletesListener)
    }

    override fun onPause() {
        super.onPause()
        requestsPath.removeEventListener(requestsListener)
        athletesPath.removeEventListener(athletesListener)
    }

    override fun onStop() {
        super.onStop()
        viewModel.requests.clear()
        viewModel.athletes.clear()
    }

    private fun showSendRequestDialog() {
        val dialogBinding = SendRequestDialogBinding.inflate(layoutInflater)

        val dialogBuilder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogBinding.sendButton.setOnClickListener {
            val email = dialogBinding.email.text.toString().lowercase()

            REF_DATABASE_ROOT.child(NODE_USERS)
                .addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
                    var coachID = ""
                    ds1.children.forEach {
                        if (it.child(USER_EMAIL).value.toString() == email) {
                            coachID = it.child("id").value.toString()
                            val dataMap = mutableMapOf<String, Any>()
                            dataMap["id"] = UID
                            dataMap[USERNAME] = USER.username
                            dataMap[USER_EMAIL] = USER.email
                            REF_DATABASE_ROOT.child(NODE_USERS).child(coachID).child(NODE_REQUESTS)
                                .child(UID).updateChildren(dataMap)

                        }
                    }
                    if (coachID == "") showToast(getString(R.string.no_email_exception))
                })
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showCoachActionsDialog(id: String) {
        val dialogBinding = CoachsActionsDialogBinding.inflate(layoutInflater)

        val dialogBuilder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        with(dialogBinding) {
            checkWorkoutsBtn.setOnClickListener {
                showChooseWorkoutDialog(id)
                dialog.dismiss()
            }
            checkStatisticsBtn.setOnClickListener {
                val action = SocialFragmentDirections.actionSocialFragmentToStatisticsFragment(id)
                dialog.dismiss()
                findNavController().navigate(action)
            }
            sendPlansBtn.setOnClickListener {
                showChoosePlanDialog(id)
                dialog.dismiss()
            }
        }
    }

    private fun showChooseWorkoutDialog(id: String) {
        val dialogBinding = ChooseItemDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogBinding.chooseItemTV.text = resources.getString(R.string.choose_workouts)

        val adapter = WorkoutAdapter(viewModel.workouts, object : WorkoutOnClickListener {
            override fun onClicked(Workout: Workout) {
                val action = SocialFragmentDirections.actionSocialFragmentToWorkoutDetailsFragment(
                    Workout.workoutId,
                    Workout.workoutName,
                    Workout.workoutDate,
                    Workout.workoutDifficulty.toString(),
                    Workout.weight.totalWeight.toFloat(),
                    id
                )
                findNavController().navigate(action)
                dialog.dismiss()
            }
        })

        val workoutsRV = dialogBinding.itemsList
        workoutsRV.layoutManager = LinearLayoutManager(context)
        workoutsRV.adapter = adapter

        workoutsPath = REF_DATABASE_ROOT.child(NODE_USERS).child(id).child(NODE_WORKOUTS)

        workoutsPath.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.workouts.clear()
            dataSnapshot.children.forEach {
                val workout = it.getValue(Workout::class.java) ?: Workout()
                viewModel.workouts.add(0, workout)
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun showChoosePlanDialog(id: String) {
        val dialogBinding = ChooseItemDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        val adapter = PlanAdapter(viewModel.plans, object : PlanOnClickListener {
            override fun onClicked(Plan: Plan) {
                showChooseRoutineDialog(Plan, id)
                dialog.dismiss()
            }
        })

        val plansRV = dialogBinding.itemsList
        plansRV.layoutManager = LinearLayoutManager(context)
        plansRV.adapter = adapter

        plansPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS)
        plansListener = AppValueEventListener { dataSnapshot ->
            viewModel.plans.clear()
            dataSnapshot.children.forEach {
                val plan = it.getValue(Plan::class.java) ?: Plan()

                viewModel.plans.add(0, plan)
            }
            adapter.notifyDataSetChanged()
        }
        plansPath.addValueEventListener(plansListener)
    }

    private fun showChooseRoutineDialog(Plan: Plan, id: String) {
        val dialogBinding = ChooseItemDialogBinding.inflate(layoutInflater)

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogBinding.chooseItemTV.text = resources.getString(R.string.choose_routine)

        routinesPath =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_PLANS).child(Plan.planId)

        val adapter = RoutineAdapter(
            requireContext(),
            viewModel.routines,
            object : RoutineOnClickListener {
                override fun onClicked(Routine: Routine) {
                    sendPlan(id, Plan, Routine)
                    dialog.dismiss()
                }
            })

        val routineRV = dialogBinding.itemsList
        routineRV.layoutManager = LinearLayoutManager(context)
        routineRV.adapter = adapter

        routinesPath.addValueEventListener(AppValueEventListener { dataSnapshot ->
            viewModel.routines.clear()
            dataSnapshot.children.forEach { dataSnapshot2 ->
                dataSnapshot2.children.forEach {
                    val routine = it.getValue(Routine::class.java) ?: Routine()
                    viewModel.routines.add(routine)
                }
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun sendPlan(id: String, Plan: Plan, Routine: Routine) {

        val dataMap = mutableMapOf<String, Any>()
        dataMap[PLAN_ID] = Plan.planId
        dataMap[PLAN_NAME] = Plan.planName

        val athletePlansPath = REF_DATABASE_ROOT.child(NODE_USERS).child(id).child(NODE_PLANS).child(Plan.planId)

        athletePlansPath.updateChildren(dataMap)

        updateRoutine(Routine, athletePlansPath)
    }

    private fun updateRoutine(routine: Routine, athletePlansPath: DatabaseReference) {

        val dataMap = mutableMapOf<String, Any>()
        dataMap[ROUTINE_DAY] = routine.routineDay
        dataMap[ROUTINE_ID] = routine.routineId
        dataMap[ROUTINE_PER_DAY_NUMBER] = routine.routinePerDayNumber
        dataMap[ROUTINE_NAME] = routine.routineName
        val routinePath = athletePlansPath.child(NODE_ROUTINES).child(routine.routineId)
        routinePath.updateChildren(dataMap)
        saveRounds(routinePath, routine)
    }

    private fun saveRounds(routinePath: DatabaseReference, routine: Routine){
        routinesPath.child(NODE_ROUTINES).child(routine.routineId).addListenerForSingleValueEvent(AppValueEventListener{ds1->
            ds1.child(NODE_EXERCISES).children.forEach {ds2->
                val exercise = ds2.getValue(Exercise::class.java) ?: Exercise()
                val dataMap = mutableMapOf<String, Any>()
                dataMap[EXERCISE_MUSCLE] = exercise.exerciseMuscleId
                dataMap[EXERCISE_TYPE_ID] = exercise.exerciseTypeId
                dataMap[EXERCISE_NAME] = exercise.exerciseName

                routinePath.child(NODE_EXERCISES).child(exercise.exerciseName).updateChildren(dataMap)
                ds2.child(NODE_ROUNDS).children.forEach {
                    val round = it.getValue(Round::class.java) ?: Round()

                    val dataMapRound = mutableMapOf<String, Any>()
                    dataMapRound["reps"] = round.reps
                    dataMapRound["restTime"] = round.restTime
                    dataMapRound["roundId"] = round.roundId
                    dataMapRound["weight"] = round.weight
                    routinePath.child(NODE_EXERCISES).child(exercise.exerciseName).child(NODE_ROUNDS).child(round.roundId).updateChildren(dataMapRound)
                }
            }
        })
    }
}
