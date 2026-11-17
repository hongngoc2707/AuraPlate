package com.example.auraplate.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.adapter.FoodAdapter
import com.example.auraplate.data.model.FoodItem
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.DialogFoodSearchBinding

class FoodSearchDialog : DialogFragment() {

    private var _binding: DialogFoodSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MockDataRepository
    private lateinit var allFoods: List<FoodItem>
    private val filteredFoods = ArrayList<FoodItem>()
    private var adapter: FoodAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFoodSearchBinding.inflate(inflater, container, false)

        repository = MockDataRepository.getInstance()
        allFoods = repository.getFoodDatabase()
        filteredFoods.addAll(allFoods)

        binding.btnCloseSearchFoodDialog.setOnClickListener { dismiss() }

        binding.rvDialogSuggestedFoods.layoutManager = LinearLayoutManager(requireContext())
        adapter = FoodAdapter(filteredFoods) { item ->
            repository.addLoggedFood(item)
            Toast.makeText(
                requireContext(),
                "✅ Đã thêm: ${item.name} (+${item.calories} kcal)",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
        binding.rvDialogSuggestedFoods.adapter = adapter

        binding.edtQuickSearchFood.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    private fun filter(query: String) {
        filteredFoods.clear()
        val q = query.trim().lowercase()
        for (item in allFoods) {
            if (q.isEmpty() || item.name.lowercase().contains(q)) {
                filteredFoods.add(item)
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
