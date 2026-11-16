package com.example.auraplate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auraplate.data.model.WeightRecord
import com.example.auraplate.databinding.ItemWeightRecordBinding

class WeightRecordAdapter(
    private val items: List<WeightRecord>
) : RecyclerView.Adapter<WeightRecordAdapter.WeightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val binding = ItemWeightRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvRecordDate.text = item.date
        holder.binding.tvRecordWeight.text = "${item.weightKg} kg"
        holder.binding.tvRecordBmi.text = "BMI: ${item.bmi}"
        holder.binding.tvRecordNote.text = item.note
    }

    override fun getItemCount(): Int = items.size

    class WeightViewHolder(val binding: ItemWeightRecordBinding) : RecyclerView.ViewHolder(binding.root)
}
