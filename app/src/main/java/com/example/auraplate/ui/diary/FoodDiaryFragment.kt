package com.example.auraplate.ui.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.adapter.FoodAdapter
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.FragmentFoodDiaryBinding
import com.example.auraplate.ui.dialog.FoodSearchDialog
import com.example.auraplate.ui.food.AddFoodActivity

class FoodDiaryFragment : Fragment() {

    private var _binding: FragmentFoodDiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MockDataRepository
    private var adapter: FoodAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDiaryBinding.inflate(inflater, container, false)
        repository = MockDataRepository.getInstance()

        setupRecyclerView()
        updateDiarySummary()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvDiaryFoods.layoutManager = LinearLayoutManager(requireContext())
        val items = repository.getTodayLoggedFoods()
        adapter = FoodAdapter(items) { item ->
            Toast.makeText(
                requireContext(),
                "${item.name} • ${item.calories} kcal (${item.portion})",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvDiaryFoods.adapter = adapter
    }

    private fun updateDiarySummary() {
        if (_binding == null) return
        val consumed = repository.getTodayTotalCaloriesIn()
        binding.tvDiaryTotalCal.text = "$consumed / 1,700 kcal"
        val percent = minOf(100, Math.round(consumed.toFloat() / 1700 * 100))
        binding.progressDiaryTotal.progress = percent
    }

    private fun setupListeners() {
        if (_binding == null) return

        binding.btnDiaryAddFood.setOnClickListener {
            val intent = Intent(requireContext(), AddFoodActivity::class.java)
            startActivity(intent)
        }

        binding.btnDiaryOpenSearchDialog.setOnClickListener {
            val dialog = FoodSearchDialog()
            dialog.show(parentFragmentManager, "FoodSearchDialog")
        }

        binding.btnPrevDay.setOnClickListener {
            Toast.makeText(requireContext(), "Xem nhật ký ngày hôm qua", Toast.LENGTH_SHORT).show()
        }

        binding.btnNextDay.setOnClickListener {
            Toast.makeText(requireContext(), "Xem nhật ký ngày mai", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
        updateDiarySummary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
