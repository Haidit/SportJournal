package com.example.sportjournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportjournal.models.Athlete

class RequestsAdapter(
    private val requests: ArrayList<Athlete>,
    private val onClickListener: AthleteOnClickListener
) :
    RecyclerView.Adapter<RequestsAdapter.RequestHolder>() {

    inner class RequestHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname: TextView = view.findViewById(R.id.nicknameTV)
        val acceptBtn: ImageButton = view.findViewById(R.id.acceptBtn)
        val rejectBtn: ImageButton = view.findViewById(R.id.rejectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.request_pods, parent, false)
        return RequestHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {
        val request = requests[position]
        holder.nickname.text = request.username
        holder.acceptBtn.setOnClickListener { onClickListener.onAcceptButtonClicked(request) }
        holder.rejectBtn.setOnClickListener { onClickListener.onRejectButtonClicked(request) }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}