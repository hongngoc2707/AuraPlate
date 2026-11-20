package com.example.auraplate.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityEditProfileBinding
import com.example.auraplate.utils.applySystemBars

/** Chỉnh sửa các chỉ số thể trạng theo đúng activity_edit_profile.xml. */
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var preferences: AppPreferences
    private var selectedGender: String = "Nam"
    private var activityLevel: String = "Vận động nhẹ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)
        preferences = AppPreferences(this)
        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        val user = preferences.getUser()
        // Figma hiện chỉ có Tuổi, Giới tính, Chiều cao, Cân nặng, Mức vận động.
        if (preferences.hasSavedProfile()) {
            binding.edtEditAge.setText(user.age.toString())
            binding.edtEditHeight.setText(user.heightCm.toString())
            binding.edtEditWeight.setText(user.currentWeightKg.toString())
        }
        selectedGender = user.gender
        binding.tvEditGender.text = selectedGender
        activityLevel = preferences.getActivityLevel()
        updateActivityLevel()
    }

    private fun setupListeners() {
        binding.btnBackEditProfile.setOnClickListener { finish() }
        binding.layoutSelectGender.setOnClickListener { showGenderPicker() }
        binding.layoutActivityLevel.setOnClickListener { showActivityLevelPicker() }
        binding.btnSaveProfile.setOnClickListener { saveProfile() }
    }

    private fun showGenderPicker() {
        val genders = arrayOf("Nam", "Nữ", "Khác")
        AlertDialog.Builder(this)
            .setTitle("Chọn giới tính")
            .setItems(genders) { _, which ->
                selectedGender = genders[which]
                binding.tvEditGender.text = selectedGender
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showActivityLevelPicker() {
        val levels = arrayOf("Ít vận động", "Vận động nhẹ", "Vận động vừa", "Vận động nhiều")
        AlertDialog.Builder(this)
            .setTitle("Mức độ vận động")
            .setItems(levels) { _, which ->
                activityLevel = levels[which]
                updateActivityLevel()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun updateActivityLevel() {
        binding.tvActivityLevelTitle.text = activityLevel
        binding.tvActivityLevelDesc.text = when (activityLevel) {
            "Ít vận động" -> "Chủ yếu ngồi, ít tập luyện"
            "Vận động nhẹ" -> "Đi bộ, thể dục 1–3 buổi/tuần"
            "Vận động vừa" -> "Tập luyện 3–5 buổi/tuần"
            else -> "Tập luyện hầu như mỗi ngày"
        }
    }

    private fun saveProfile() {
        val age = binding.edtEditAge.text.toString().trim().toIntOrNull()
        val height = binding.edtEditHeight.text.toString().trim().toFloatOrNull()
        val weight = binding.edtEditWeight.text.toString().trim().toFloatOrNull()

        if (age == null || age !in 1..120 || height == null || height <= 0f ||
            weight == null || weight <= 0f || !height.isFinite() || !weight.isFinite()) {
            Toast.makeText(this, "Vui lòng nhập tuổi, chiều cao và cân nặng hợp lệ", Toast.LENGTH_LONG).show()
            return
        }

        val wasSaved = preferences.hasSavedProfile()
        val user = preferences.getUser()
        val previousWeight = if (wasSaved) user.currentWeightKg else 0f
        user.age = age
        user.gender = selectedGender
        user.heightCm = height
        user.currentWeightKg = weight
        preferences.saveUser(user)
        preferences.markProfileCompleted(weight, height)
        if (wasSaved && kotlin.math.abs(previousWeight - weight) > 0.05f) {
            val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("vi", "VN"))
                .format(java.util.Date())
            val bmi = com.example.auraplate.utils.BmiCalculator.calculateBmi(weight, height)
            com.example.auraplate.data.local.WeightHistoryStore.addRecord(
                this, com.example.auraplate.data.model.WeightRecord(today, weight, bmi, "Cập nhật hồ sơ")
            )
        }
        preferences.saveActivityLevel(activityLevel)
        Toast.makeText(this, "Đã lưu thay đổi hồ sơ", Toast.LENGTH_SHORT).show()
        finish()
    }
}
