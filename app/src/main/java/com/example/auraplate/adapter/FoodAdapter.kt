package com.example.auraplate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auraplate.data.model.FoodItem
import com.example.auraplate.databinding.ItemFoodBinding

class FoodAdapter(
    private val items: List<FoodItem>,
    private val listener: ((FoodItem) -> Unit)? = null
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvFoodName.text = item.name
        holder.binding.tvFoodPortion.text = item.portion
        holder.binding.tvFoodCalories.text = "${item.calories} kcal"
        holder.binding.tvFoodMacros.text = String.format("C: %.0fg • P: %.0fg • F: %.0fg", item.carbsGrams, item.proteinGrams, item.fatGrams)

        holder.itemView.setOnClickListener {
            listener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class FoodViewHolder(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)
}
