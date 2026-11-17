package com.example.auraplate.ui.profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.auraplate.MainActivity
import com.example.auraplate.R
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivitySetupProfileBinding
import com.example.auraplate.utils.BmiCalculator

class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private var selectedGender: String = "Nam"
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        setupListeners()
        calculateAndDisplayBmi()
    }

    private fun setupListeners() {
        binding.btnGenderMale.setOnClickListener { selectGender("Nam") }
        binding.btnGenderFemale.setOnClickListener { selectGender("Nữ") }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayBmi()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.edtHeight.addTextChangedListener(watcher)
        binding.edtCurrentWeight.addTextChangedListener(watcher)

        binding.btnCompleteProfile.setOnClickListener { saveProfileAndProceed() }
    }

    private fun selectGender(gender: String) {
        selectedGender = gender
        if ("Nam" == gender) {
            binding.btnGenderMale.setBackgroundResource(R.drawable.bg_segment_active)
            binding.btnGenderMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnGenderFemale.background = null
            binding.btnGenderFemale.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        } else {
            binding.btnGenderFemale.setBackgroundResource(R.drawable.bg_segment_active)
            binding.btnGenderFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.btnGenderMale.background = null
            binding.btnGenderMale.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun calculateAndDisplayBmi() {
        try {
            val height = binding.edtHeight.text.toString().trim().toFloat()
            val weight = binding.edtCurrentWeight.text.toString().trim().toFloat()
            if (height > 0 && weight > 0) {
                val bmi = BmiCalculator.calculateBmi(weight, height)
                binding.tvBmiNumber.text = String.format("%.1f", bmi)
                val status = BmiCalculator.getBmiStatus(bmi)
                binding.tvBmiStatusTag.text = status.uppercase()
                binding.tvBmiDescription.text = "💡 Thể trạng hiện tại: $status. AuraPlate đã hiệu chỉnh kế hoạch dinh dưỡng phù hợp cho bạn."
            }
        } catch (_: Exception) {}
    }

    private fun saveProfileAndProceed() {
        try {
            val age = binding.edtAge.text.toString().trim().toInt()
            val height = binding.edtHeight.text.toString().trim().toFloat()
            val currentWeight = binding.edtCurrentWeight.text.toString().trim().toFloat()
            val targetWeight = binding.edtTargetWeight.text.toString().trim().toFloat()

            val user = preferences.getUser()
            user.gender = selectedGender
            user.age = age
            user.heightCm = height
            user.currentWeightKg = currentWeight
            user.targetWeightKg = targetWeight
            preferences.saveUser(user)
            preferences.isLoggedIn = true

            Toast.makeText(this, "Hồ sơ đã được lưu thành công!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (_: Exception) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ và chính xác các chỉ số", Toast.LENGTH_SHORT).show()
        }
    }
}
