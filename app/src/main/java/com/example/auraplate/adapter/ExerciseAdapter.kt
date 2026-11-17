package com.example.auraplate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auraplate.data.model.ExerciseItem
import com.example.auraplate.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val items: List<ExerciseItem>,
    private val listener: ((ExerciseItem) -> Unit)? = null
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvExerciseName.text = item.name
        holder.binding.tvExerciseDuration.text = "${item.durationMinutes} phút • ${item.category}"
        holder.binding.tvExerciseCalories.text = "-${item.caloriesBurned} kcal"
        holder.binding.tvExerciseTime.text = item.timeRecorded

        holder.itemView.setOnClickListener {
            listener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ExerciseViewHolder(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)
}
