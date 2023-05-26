package com.example.sportjournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Athlete
import com.example.sportjournal.models.User

class AthletesAdapter(
    private val athletes: ArrayList<Athlete>,
    private val onClickListener: AthleteOnClickListener
) :
    RecyclerView.Adapter<AthletesAdapter.AthleteHolder>() {

    inner class AthleteHolder(view: View) : RecyclerView.ViewHolder(view) {

        val athleteCard: CardView = view.findViewById(R.id.athleteCard)
        val nickname: TextView = view.findViewById(R.id.nicknameTV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AthleteHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.athlete_pods, parent, false)
        return AthleteHolder(itemView)
    }

    override fun onBindViewHolder(holder: AthleteHolder, position: Int) {
        val athlete = athletes[position]
        holder.nickname.text = athlete.username
        holder.athleteCard.setOnClickListener { onClickListener.onClicked(athlete) }
    }

    override fun getItemCount(): Int {
        return athletes.size
    }
}