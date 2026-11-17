package com.example.auraplate.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.adapter.ExerciseAdapter
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.FragmentExerciseBinding

class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: MockDataRepository
    private var adapter: ExerciseAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        repository = MockDataRepository.getInstance()

        setupRecyclerView()
        updateSummary()
        setupListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvExerciseList.layoutManager = LinearLayoutManager(requireContext())
        val items = repository.getTodayLoggedExercises()
        adapter = ExerciseAdapter(items) { item ->
            Toast.makeText(
                requireContext(),
                "${item.name} • ${item.durationMinutes} phút đốt ${item.caloriesBurned} kcal",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvExerciseList.adapter = adapter
    }

    private fun updateSummary() {
        if (_binding == null) return
        val totalBurned = repository.getTodayTotalCaloriesBurned()
        binding.tvExerciseTotalBurned.text = totalBurned.toString()

        var totalMin = 0
        for (item in repository.getTodayLoggedExercises()) {
            totalMin += item.durationMinutes
        }
        binding.tvExerciseTotalMinutes.text = "$totalMin phút"
    }

    private fun setupListeners() {
        if (_binding == null) return
        binding.btnExerciseAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
        updateSummary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
