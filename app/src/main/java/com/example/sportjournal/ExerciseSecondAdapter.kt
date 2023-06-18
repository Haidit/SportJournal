package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.databinding.EditRoundDialogBinding
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Round

class ExerciseSecondAdapter(
    private val exercisesGroups: ArrayList<Pair<Exercise, ArrayList<Round>>>,
    private val context: Context,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<ExerciseSecondAdapter.ExerciseHolder>() {

    private lateinit var innerAdapter: RecyclerView.Adapter<RoundAdapter.RoundHolder>

    inner class ExerciseHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseCard: CardView = view.findViewById(R.id.exercise_card)
        val exerciseName: TextView = view.findViewById(R.id.exercise_name)
        val addButton: ImageView = view.findViewById(R.id.add_button)
        val innerLayout: LinearLayout = view.findViewById(R.id.innerLayout)
        val innerRV: RecyclerView = view.findViewById(R.id.innerRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_pods, parent, false)
        return ExerciseHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        val exerciseGroup = exercisesGroups[position]
        holder.exerciseName.text = exerciseGroup.first.exerciseName

        innerAdapter = RoundAdapter(context, exerciseGroup.second, object : RoundOnClickListener {
            override fun onClicked(Round: Round) {
                showEditRoundDialog(Round, exerciseGroup.second)
            }
        })
        holder.innerRV.layoutManager = LinearLayoutManager(context)
        holder.innerRV.adapter = innerAdapter

        val isExpanded: Boolean = exerciseGroup.first.active
        var count = 0
        holder.innerLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.exerciseCard.setOnClickListener {
            exerciseGroup.first.active = !exerciseGroup.first.active
            notifyItemChanged(position)
        }

        holder.addButton.setOnClickListener {
            count++
            exerciseGroup.second.add(Round(roundId = count.toString()))
            innerAdapter.notifyDataSetChanged()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return exercisesGroups.size
    }

    private fun showEditRoundDialog(round: Round, rounds: ArrayList<Round>) {
        val dialogBinding = EditRoundDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(context).setView(dialogBinding.root)
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
            if (weightView.text.toString() == "") round.weight = 0
            else round.weight = weightView.text.toString().toInt()
            if (repeatsView.text.toString() == "") round.reps = 0
            else round.reps = repeatsView.text.toString().toInt()
            if (restView.text.toString() == "") round.restTime = 0
            else round.restTime = restView.text.toString().toInt()
            innerAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        negativeButton.setOnClickListener {
            rounds.remove(round)
            innerAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }
}