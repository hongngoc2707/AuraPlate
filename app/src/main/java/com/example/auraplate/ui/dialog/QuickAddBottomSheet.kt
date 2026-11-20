package com.example.auraplate.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.widget.FrameLayout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auraplate.databinding.BottomSheetQuickAddBinding
import com.example.auraplate.ui.exercise.AddExerciseActivity
import com.example.auraplate.ui.food.AddFoodActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

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

        binding.btnQuickCancel.setOnClickListener { dismiss() }

        return binding.root
    }

    /** Bỏ nền chữ nhật mặc định của BottomSheet để thấy góc bo theo Figma. */
    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)
            ?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
