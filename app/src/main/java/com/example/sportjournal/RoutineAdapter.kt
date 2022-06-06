package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Routine

class RoutineAdapter(
    private val context: Context,
    private val routines: ArrayList<Routine>,
    private val onClickListener: RoutineOnClickListener
) :
    RecyclerView.Adapter<RoutineAdapter.RoutineHolder>() {

    inner class RoutineHolder(view: View) : RecyclerView.ViewHolder(view) {
        val routineCard: CardView = view.findViewById(R.id.routineCard)
        val routineName: TextView = view.findViewById(R.id.routine_name)
        val routineDay: TextView = view.findViewById(R.id.routine_day)
        val routineNumber: TextView = view.findViewById(R.id.routine_number_per_day)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.routines_pods, parent, false)
        return RoutineHolder(itemView)
    }

    override fun onBindViewHolder(holder: RoutineHolder, position: Int) {
        val routine = routines[position]
        holder.routineName.text = routine.routineName
        holder.routineDay.text = context.getString(R.string.routineDay, routine.routineDay)
        holder.routineNumber.text = context.getString(R.string.routineNumber, routine.routinePerDayNumber)
        holder.routineCard.setOnClickListener {
            onClickListener.onClicked(routine)
        }
    }

    override fun getItemCount(): Int {
        return routines.size
    }
}