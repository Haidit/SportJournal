package com.example.sportjournal.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sportjournal.R
import com.example.sportjournal.utilits.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import java.util.*


class CreateWorkout2Fragment : Fragment(R.layout.fragment_create_workout2) {

    private lateinit var workoutId: String
    private lateinit var workoutPath: DatabaseReference
    private lateinit var workoutName: TextInputEditText
    private lateinit var workoutDate: TextInputEditText
    private lateinit var workoutDifficulty: TextView
    private lateinit var seekBar: SeekBar
    private var difficulty = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: CreateWorkout2FragmentArgs by navArgs()
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        workoutDate = view.findViewById(R.id.new_workout_date)
        workoutDifficulty = view.findViewById(R.id.difficulty_text)
        seekBar = view.findViewById(R.id.seekbar)
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                difficulty = p1
                workoutDifficulty.text = resources.getString(R.string.workout_difficulty, difficulty)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        workoutDifficulty.text = resources.getString(R.string.workout_difficulty, difficulty)

        workoutId = args.workoutId

        workoutPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(
            NODE_WORKOUTS
        ).child(workoutId)

        view.findViewById<ImageButton>(R.id.date_btn).setOnClickListener {
            val dpd = DatePickerDialog(this.requireContext(), { _, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val mmmMonth = if (mmMonth >= 10) mmMonth.toString() else "0$mmMonth"
                val mmDay = if (mDay >= 10) mDay.toString() else "0$mDay"
                workoutDate.setText("$mmDay.$mmmMonth.$mYear")
            }, year, month, day)
            dpd.show()
        }

        view.findViewById<Button>(R.id.doneBtn).setOnClickListener {
            workoutName = view.findViewById(R.id.new_workout_name)
            workoutDate = view.findViewById(R.id.new_workout_date)

            if (validateForm(workoutName) && validateForm(workoutDate)
            ) {
                finishWorkout(workoutPath)
                findNavController().navigate(R.id.action_createWorkout2Fragment_to_workoutsFragment)
            }
        }
    }

    private fun finishWorkout(workoutPath: DatabaseReference) {
        workoutPath.child(WORKOUT_NAME).setValue(workoutName.text.toString())
        workoutPath.child(WORKOUT_DATE).setValue(workoutDate.text.toString())
        workoutPath.child(WORKOUT_DIFFICULTY)
            .setValue(difficulty)
    }
}