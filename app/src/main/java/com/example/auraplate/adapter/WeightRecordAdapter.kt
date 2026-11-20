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
        holder.binding.tvRecordWeight.text = "%.1f kg".format(java.util.Locale("vi", "VN"), item.weightKg)
        holder.binding.tvRecordBmi.text = if (item.bmi > 0f) {
            "BMI: %.1f".format(java.util.Locale("vi", "VN"), item.bmi)
        } else "BMI: --"
        holder.binding.tvRecordNote.text = item.note.ifBlank { "Đã cập nhật cân nặng" }
    }

    override fun getItemCount(): Int = items.size

    class WeightViewHolder(val binding: ItemWeightRecordBinding) : RecyclerView.ViewHolder(binding.root)
}
