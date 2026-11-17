package com.example.auraplate.ui.food

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.R
import com.example.auraplate.adapter.FoodAdapter
import com.example.auraplate.data.model.FoodItem
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.ActivityAddFoodBinding

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var repository: MockDataRepository
    private lateinit var allFoods: List<FoodItem>
    private val filteredFoods = ArrayList<FoodItem>()
    private lateinit var adapter: FoodAdapter
    private var selectedCategory = "Tất cả"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = MockDataRepository.getInstance()
        allFoods = repository.getFoodDatabase()
        filteredFoods.addAll(allFoods)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvSearchFoods.layoutManager = LinearLayoutManager(this)
        adapter = FoodAdapter(filteredFoods) { item ->
            repository.addLoggedFood(item)
            Toast.makeText(
                this,
                "✅ Đã thêm: ${item.name} (+${item.calories} kcal)",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
        binding.rvSearchFoods.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackAddFood.setOnClickListener { finish() }

        binding.btnClearSearch.setOnClickListener {
            binding.edtSearchFood.setText("")
            filterList()
        }

        binding.edtSearchFood.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.btnClearSearch.visibility = if ((s?.length ?: 0) > 0) View.VISIBLE else View.GONE
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipAll.setOnClickListener { selectCategory("Tất cả", binding.chipAll) }
        binding.chipNoodle.setOnClickListener { selectCategory("Món nước", binding.chipNoodle) }
        binding.chipRice.setOnClickListener { selectCategory("Cơm / Mì", binding.chipRice) }
        binding.chipMeat.setOnClickListener { selectCategory("Thịt / Cá", binding.chipMeat) }
        binding.chipVeggie.setOnClickListener { selectCategory("Rau củ", binding.chipVeggie) }
    }

    private fun selectCategory(cat: String, activeChip: TextView) {
        selectedCategory = cat
        val chips = arrayOf(binding.chipAll, binding.chipNoodle, binding.chipRice, binding.chipMeat, binding.chipVeggie)
        for (c in chips) {
            if (c == activeChip) {
                c.setBackgroundResource(R.drawable.bg_segment_active)
                c.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                c.setBackgroundResource(R.drawable.bg_pill_badge)
                c.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
        filterList()
    }

    private fun filterList() {
        val query = binding.edtSearchFood.text.toString().trim().lowercase()
        filteredFoods.clear()

        for (item in allFoods) {
            val matchCat = selectedCategory == "Tất cả" || item.category.contains(selectedCategory)
            val matchQuery = query.isEmpty() || item.name.lowercase().contains(query)
            if (matchCat && matchQuery) {
                filteredFoods.add(item)
            }
        }

        binding.tvFoodCountHeader.text = "Tìm thấy ${filteredFoods.size} món ăn phù hợp"
        adapter.notifyDataSetChanged()
    }
}
