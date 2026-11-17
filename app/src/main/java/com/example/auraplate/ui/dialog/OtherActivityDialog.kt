package com.example.auraplate.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.auraplate.data.model.ExerciseItem
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.DialogOtherActivityBinding
import com.example.auraplate.utils.DateFormatter

class OtherActivityDialog : DialogFragment() {

    private var _binding: DialogOtherActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogOtherActivityBinding.inflate(inflater, container, false)

        binding.btnCloseOtherDialog.setOnClickListener { dismiss() }
        binding.btnCancelOtherDialog.setOnClickListener { dismiss() }

        binding.btnConfirmOtherDialog.setOnClickListener {
            val name = binding.edtOtherActivityName.text.toString().trim()
            val durStr = binding.edtOtherActivityDuration.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                binding.edtOtherActivityName.error = "Vui lòng nhập tên hoạt động"
                return@setOnClickListener
            }

            var duration = 30
            try {
                duration = durStr.toInt()
            } catch (ignored: Exception) {}

            val burned = duration * 6 // trung bình 6 kcal/phút
            val item = ExerciseItem(
                id = "OE_${System.currentTimeMillis()}",
                name = name,
                durationMinutes = duration,
                caloriesBurned = burned,
                category = "Khác",
                timeRecorded = DateFormatter.getCurrentTime()
            )
            MockDataRepository.getInstance().addLoggedExercise(item)

            Toast.makeText(
                requireContext(),
                "✅ Đã thêm: $name ($duration phút, -$burned kcal)",
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
