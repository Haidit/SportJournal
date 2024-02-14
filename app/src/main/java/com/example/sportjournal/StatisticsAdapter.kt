package com.example.sportjournal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class StatisticsAdapter(
    private val statistics: ArrayList<Pair<String, Float>>,
    private val totalWeight: Float,
    private val context: Context
) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsHolder>() {

    inner class StatisticsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exType: TextView = view.findViewById(R.id.exType)
        val kg: TextView = view.findViewById(R.id.kg)
        val percent: TextView = view.findViewById(R.id.percent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.statistics_main_pods, parent, false)
        return StatisticsHolder(itemView)
    }

    override fun onBindViewHolder(holder: StatisticsHolder, position: Int) {
        val stat = statistics[position]
        holder.exType.text = stat.first
        holder.kg.text = context.getString(R.string.weight, stat.second.toInt())
        val perc =
            if ((stat.second / totalWeight * 100).isNaN()) 0 else (stat.second / totalWeight * 100).roundToInt()
        holder.percent.text = context.getString(R.string.percent, perc)
    }

    override fun getItemCount(): Int {
        return statistics.size
    }
}