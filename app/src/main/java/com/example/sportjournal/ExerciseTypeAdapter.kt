package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.models.Routine

class ExerciseTypeAdapter(
    private val exerciseTypes: ArrayList<Pair<String,ArrayList<Exercise>>>,
    context: Context
) :
    RecyclerView.Adapter<ExerciseTypeAdapter.ExerciseTypeHolder>() {

    val innerContext = context
    inner class ExerciseTypeHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseTypeCard: CardView = view.findViewById(R.id.exerciseTypeCard)
        val exerciseTypeName: TextView = view.findViewById(R.id.exerciseType)
        val innerLayout: LinearLayout = view.findViewById(R.id.innerLayout)
        val innerRV: RecyclerView = view.findViewById(R.id.innerRV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseTypeHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.exercise_types_pods, parent, false)
        return ExerciseTypeHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseTypeHolder, position: Int) {
        val exerciseType = exerciseTypes[position]
        holder.exerciseTypeName.text = exerciseType.first

        val innerAdapter = ExerciseAdapter(exerciseType.second)
        holder.innerRV.layoutManager = LinearLayoutManager(innerContext)
        holder.innerRV.adapter = innerAdapter

        holder.exerciseTypeCard.setOnClickListener {
            holder.innerLayout.isVisible = !(holder.innerLayout.isVisible)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return exerciseTypes.size
    }
}