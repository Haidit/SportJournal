package com.example.sportjournal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Exercise

class ExerciseAdapter(
    val exercises: ArrayList<Exercise>,
    private val context: Context
) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseHolder>() {

    inner class ExerciseHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseCard: CardView = view.findViewById(R.id.exercise_card)
        val exerciseName: TextView = view.findViewById(R.id.exercise_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_to_choose_pods, parent, false)
        return ExerciseHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        val exercise = exercises[position]
        val name =
            if (exercise.active) exercise.exerciseName + " " + context.getString(R.string.added) else exercise.exerciseName
        holder.exerciseName.text = name
        holder.exerciseCard.setOnClickListener {
            exercise.active = !exercise.active
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}