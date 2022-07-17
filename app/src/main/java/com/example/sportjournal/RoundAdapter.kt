package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Round

class RoundAdapter(
    private val context: Context,
    private val rounds: ArrayList<Round>,
    private val onClickListener: RoundOnClickListener
) :
    RecyclerView.Adapter<RoundAdapter.RoundHolder>() {

    inner class RoundHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roundCard: CardView = view.findViewById(R.id.roundCard)
        val reps: TextView = view.findViewById(R.id.exercise_repeats_number)
        val weight: TextView = view.findViewById(R.id.exercise_weight)
        val restTime: TextView = view.findViewById(R.id.exercise_rest_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.round_pods, parent, false)
        return RoundHolder(itemView)
    }

    override fun onBindViewHolder(holder: RoundHolder, position: Int) {
        val round = rounds[position]
        holder.reps.text = context.getString(R.string.reps, round.reps)
        holder.weight.text = context.getString(R.string.weight, round.weight)
        holder.restTime.text = context.getString(R.string.rest, round.restTime)
        holder.roundCard.setOnClickListener {
            onClickListener.onClicked(round)
        }
    }

    override fun getItemCount(): Int {
        return rounds.size
    }
}