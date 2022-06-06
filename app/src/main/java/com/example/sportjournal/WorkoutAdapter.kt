package com.example.sportjournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Workout

class WorkoutAdapter(
    private val workouts: ArrayList<Workout>,
    private val onClickListener: WorkoutOnClickListener
) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutHolder>() {

    inner class WorkoutHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutCard: CardView = view.findViewById(R.id.workoutCard)
        val workoutName: TextView = view.findViewById(R.id.workout_name)
        val workoutDate: TextView = view.findViewById(R.id.workout_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.workout_pods, parent, false)
        return WorkoutHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutName.text = workout.workoutName
        holder.workoutDate.text = workout.workoutDate
        holder.workoutCard.setOnClickListener {
            onClickListener.onClicked(workout)
        }
    }

    override fun getItemCount(): Int {
        return workouts.size
    }
}