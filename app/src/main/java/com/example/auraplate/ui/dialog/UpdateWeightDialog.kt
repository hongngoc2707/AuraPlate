package com.example.auraplate.ui.dialog

import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.data.model.WeightRecord
import com.example.auraplate.data.local.WeightHistoryStore
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
        if (prefs.hasSavedProfile()) {
            binding.edtDialogWeight.setText(prefs.getUser().currentWeightKg.toString())
        } else {
            binding.edtDialogWeight.text?.clear()
        }
        binding.edtDialogNote.text?.clear()

        binding.btnCancelDialog.setOnClickListener { dismiss() }

        binding.btnSaveDialog.setOnClickListener {
            try {
                val weight = binding.edtDialogWeight.text.toString().trim().toFloat()
                if (!weight.isFinite() || weight < 15f || weight > 350f) {
                    binding.edtDialogWeight.error = "Nhập số cân hợp lệ (15–350 kg)"
                    return@setOnClickListener
                }
                if (!prefs.hasSavedProfile()) {
                    Toast.makeText(requireContext(), "Vui lòng thiết lập hồ sơ trước", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val note = binding.edtDialogNote.text.toString().trim()

                val user = prefs.getUser()
                user.currentWeightKg = weight
                prefs.saveUser(user)

                val bmi = BmiCalculator.calculateBmi(weight, user.heightCm)
                val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("vi", "VN"))
                    .format(java.util.Date())
                WeightHistoryStore.addRecord(requireContext(), WeightRecord(today, weight, bmi, note))

                parentFragmentManager.setFragmentResult("weight_updated", Bundle())
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

    /** Trong suốt bên ngoài để thẻ cập nhật cân nặng bo góc trọn vẹn. */
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.91f).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setDimAmount(0.35f)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
