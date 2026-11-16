package com.example.auraplate.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.BottomSheetQuickAddBinding
import com.example.auraplate.ui.exercise.AddExerciseActivity
import com.example.auraplate.ui.food.AddFoodActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuickAddBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetQuickAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetQuickAddBinding.inflate(inflater, container, false)

        binding.btnQuickAddFood.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), AddFoodActivity::class.java))
        }

        binding.btnQuickAddExercise.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), AddExerciseActivity::class.java))
        }

        binding.btnQuickLogWeight.setOnClickListener {
            dismiss()
            val dialog = UpdateWeightDialog()
            dialog.show(parentFragmentManager, "UpdateWeightDialog")
        }

        binding.btnQuickAddWater.setOnClickListener {
            val prefs = AppPreferences(requireContext())
            prefs.addWater(250)
            Toast.makeText(
                requireContext(),
                "💧 Đã ghi nhận +250ml nước! Đạt ${prefs.getWaterIntakeMl()}ml / 2000ml",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
