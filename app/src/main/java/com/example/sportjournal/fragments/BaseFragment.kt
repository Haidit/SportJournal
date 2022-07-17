package com.example.sportjournal.fragments

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.sportjournal.databinding.EditRoundDialogBinding
import com.example.sportjournal.models.Round
import com.example.sportjournal.utilits.NODE_ROUNDS
import com.google.firebase.database.DatabaseReference

open class BaseFragment(layout: Int) : Fragment(layout) {

    private var progressBar: ProgressBar? = null

    fun setProgressBar(bar: ProgressBar) {
        progressBar = bar
    }

    fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.INVISIBLE
    }

    fun showEditRoundDialog(round: Round, reference: DatabaseReference) {
        val currentRound = reference.child(NODE_ROUNDS).child(round.roundId)
        val dialogBinding = EditRoundDialogBinding.inflate(layoutInflater)
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
        val positiveButton = dialogBinding.saveButton
        val negativeButton = dialogBinding.deleteButton
        val weightView = dialogBinding.exerciseWeightPicker
        val repeatsView = dialogBinding.exerciseRepeats
        val restView = dialogBinding.restTime
        weightView.setText(round.weight.toString())
        repeatsView.setText(round.reps.toString())
        restView.setText(round.restTime.toString())
        val dialog = dialogBuilder.create()
        dialog.show()
        positiveButton.setOnClickListener {
            if (weightView.text.toString() == "") currentRound.child("weight").setValue(0)
            else currentRound.child("weight").setValue(weightView.text.toString().toInt())
            if (repeatsView.text.toString() == "") currentRound.child("reps").setValue(0)
            else currentRound.child("reps").setValue(repeatsView.text.toString().toInt())
            if (restView.text.toString() == "") currentRound.child("restTime").setValue(0)
            else currentRound.child("restTime").setValue(restView.text.toString().toInt())
            dialog.dismiss()
        }
        negativeButton.setOnClickListener {
            currentRound.removeValue()
            dialog.dismiss()
        }
    }
}