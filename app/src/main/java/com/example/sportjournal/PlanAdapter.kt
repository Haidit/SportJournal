package com.example.sportjournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Plan
import com.example.sportjournal.models.Workout

class PlanAdapter(
    private val plans: ArrayList<Plan>,
    private val onClickListener: PlanOnClickListener
) :
    RecyclerView.Adapter<PlanAdapter.PlanHolder>() {

    inner class PlanHolder(view: View) : RecyclerView.ViewHolder(view) {
        val planCard: CardView = view.findViewById(R.id.plan_card)
        val planName: TextView = view.findViewById(R.id.plan_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.plans_pods, parent, false)
        return PlanHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlanHolder, position: Int) {
        val plan = plans[position]
        holder.planName.text = plan.planName
        holder.planCard.setOnClickListener {
            onClickListener.onClicked(plan)
        }
    }

    override fun getItemCount(): Int {
        return plans.size
    }
}