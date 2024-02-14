package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.ExerciseGroup

class ExerciseTypeAdapter(
    val exerciseGroups: ArrayList<ExerciseGroup>,
    private val context: Context
) :
    RecyclerView.Adapter<ExerciseTypeAdapter.ExerciseTypeHolder>() {

    inner class ExerciseTypeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseTypeCard: CardView = view.findViewById(R.id.exerciseTypeCard)
        val exerciseTypeName: TextView = view.findViewById(R.id.exerciseType)
        val innerLayout: RelativeLayout = view.findViewById(R.id.innerLayout)
        val innerRV: RecyclerView = view.findViewById(R.id.innerRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseTypeHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_types_pods, parent, false)
        return ExerciseTypeHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseTypeHolder, position: Int) {
        val exerciseType = exerciseGroups[position]
        holder.exerciseTypeName.text = exerciseType.exercisePair.first

        if (CHOOSE_EXERCISE_MODE) {
            val innerAdapter = ExerciseAdapter(exerciseType.exercisePair.second, context)
            holder.innerRV.layoutManager = LinearLayoutManager(context)
            holder.innerRV.adapter = innerAdapter
        } else {
            val innerAdapter = ExercisePicturesAdapter(exerciseType.exercisePair.second, context)
            holder.innerRV.layoutManager = GridLayoutManager(context, 3)
            holder.innerRV.adapter = innerAdapter
        }

        val isExpanded: Boolean = exerciseType.expanded
        holder.innerLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.exerciseTypeCard.setOnClickListener {
            exerciseType.expanded = !exerciseType.expanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return exerciseGroups.size
    }
}