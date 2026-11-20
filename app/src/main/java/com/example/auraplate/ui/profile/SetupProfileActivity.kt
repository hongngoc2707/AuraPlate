package com.example.auraplate.ui.profile

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.MainActivity
import com.example.auraplate.ui.auth.LoginActivity
import com.example.auraplate.ui.auth.RegisterActivity
import com.example.auraplate.utils.applySystemBars
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivitySetupProfileBinding

/** Thiết lập hồ sơ ban đầu theo đúng activity_setup_profile.xml. */
class SetupProfileActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FROM_REGISTER = "from_register"
    }

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var preferences: AppPreferences
    private var selectedGender: String = "Nam"
    private var activityLevel: String = "Vận động nhẹ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)
        preferences = AppPreferences(this)
        loadExistingProfile()
        setupListeners()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = returnToPreviousStep()
            }
        )
    }

    private fun loadExistingProfile() {
        val user = preferences.getUser()
        // Không dùng các trường TargetWeight/BMI vì giao diện Figma không có.
        selectedGender = user.gender
        activityLevel = preferences.getActivityLevel()
        binding.tvEditGender.text = selectedGender
        updateActivityLevel()
        // Giữ ô trống khi mới thiết lập để người dùng chủ động nhập.
    }

    private fun setupListeners() {
        binding.btnBackEditProfile.setOnClickListener { returnToPreviousStep() }
        binding.layoutSelectGender.setOnClickListener { showGenderPicker() }
        binding.layoutActivityLevel.setOnClickListener { showActivityLevelPicker() }
        binding.btnSaveProfile.setOnClickListener { saveProfileAndProceed() }
    }

    /**
     * Đăng ký -> Thiết lập: quay về Đăng ký.
     * Đăng nhập -> Thiết lập (hồ sơ chưa có): quay về Đăng nhập.
     * CLEAR_TOP giữ biểu mẫu đã nhập nếu Activity trước vẫn còn trong task.
     */
    private fun returnToPreviousStep() {
        val fromRegister = intent.getBooleanExtra(EXTRA_FROM_REGISTER, false)
        val destination = if (fromRegister) RegisterActivity::class.java else LoginActivity::class.java
        val backIntent = Intent(this, destination).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(backIntent)
        finish()
    }

    private fun showGenderPicker() {
        val genders = arrayOf("Nam", "Nữ", "Khác")
        AlertDialog.Builder(this)
            .setTitle("Chọn giới tính")
            .setItems(genders) { _, index ->
                selectedGender = genders[index]
                binding.tvEditGender.text = selectedGender
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showActivityLevelPicker() {
        val levels = arrayOf("Ít vận động", "Vận động nhẹ", "Vận động vừa", "Vận động nhiều")
        AlertDialog.Builder(this)
            .setTitle("Mức độ vận động")
            .setItems(levels) { _, index ->
                activityLevel = levels[index]
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

    private fun saveProfileAndProceed() {
        val age = binding.edtEditAge.text.toString().trim().toIntOrNull()
        val height = binding.edtEditHeight.text.toString().trim().toFloatOrNull()
        val weight = binding.edtEditWeight.text.toString().trim().toFloatOrNull()

        if (age == null || age !in 1..120 || height == null || height <= 0f ||
            weight == null || weight <= 0f || !height.isFinite() || !weight.isFinite()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tuổi, chiều cao và cân nặng", Toast.LENGTH_LONG).show()
            return
        }

        val user = preferences.getUser()
        user.age = age
        user.gender = selectedGender
        user.heightCm = height
        user.currentWeightKg = weight
        preferences.saveUser(user)
        preferences.markProfileCompleted(weight, height)
        preferences.saveActivityLevel(activityLevel)
        preferences.isLoggedIn = true
        Toast.makeText(this, "Đã lưu hồ sơ", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
