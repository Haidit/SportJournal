package com.example.sportjournal

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportjournal.models.Exercise
import com.example.sportjournal.utilits.STORAGE_ROOT
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

class ExercisePicturesAdapter(
    private val exercises: ArrayList<Exercise>,
    private val context: Context
) :
    RecyclerView.Adapter<ExercisePicturesAdapter.ExerciseHolder>() {

    inner class ExerciseHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseCard: CardView = view.findViewById(R.id.exercise_card)
        val image: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.exercise_to_choose_pics_pods, parent, false)
        return ExerciseHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        val exercise = exercises[position]
        val url = exercise.imageUrl
        Glide.with(context).load(url).override(64, 64).into(holder.image)
        if (exercise.active) {
            holder.exerciseCard.setBackgroundColor(context.resources.getColor(R.color.green))
        } else {
            holder.exerciseCard.setBackgroundColor(context.resources.getColor(R.color.white))
        }
        holder.exerciseCard.setOnClickListener {
            exercise.active = !exercise.active
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}