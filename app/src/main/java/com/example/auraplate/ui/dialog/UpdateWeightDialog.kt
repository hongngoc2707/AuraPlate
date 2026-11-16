package com.example.auraplate.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.data.model.WeightRecord
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.DialogUpdateWeightBinding
import com.example.auraplate.utils.BmiCalculator

class UpdateWeightDialog : DialogFragment() {

    private var _binding: DialogUpdateWeightBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUpdateWeightBinding.inflate(inflater, container, false)

        val prefs = AppPreferences(requireContext())
        binding.edtDialogWeight.setText(prefs.getUser().currentWeightKg.toString())

        binding.btnCancelDialog.setOnClickListener { dismiss() }

        binding.btnSaveDialog.setOnClickListener {
            try {
                val weight = binding.edtDialogWeight.text.toString().trim().toFloat()
                val note = binding.edtDialogNote.text.toString().trim()

                val user = prefs.getUser()
                user.currentWeightKg = weight
                prefs.saveUser(user)

                val bmi = BmiCalculator.calculateBmi(weight, user.heightCm)
                MockDataRepository.getInstance().addWeightRecord(WeightRecord("Hôm nay", weight, bmi, note))

                Toast.makeText(
                    requireContext(),
                    "✅ Đã ghi nhận cân nặng: $weight kg (BMI: ${String.format("%.1f", bmi)})",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Vui lòng nhập số cân hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
