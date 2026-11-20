package com.example.auraplate.ui.goal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.MainActivity
import com.example.auraplate.R
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityGoalSettingBinding
import com.example.auraplate.ui.dialog.QuickAddBottomSheet
import com.example.auraplate.utils.applySystemBars

class GoalSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalSettingBinding
    private lateinit var preferences: AppPreferences
    private var goalType = "Giảm cân"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)
        preferences = AppPreferences(this)
        val user = preferences.getUser()
        binding.tvCurrentGoalWeight.text = if (preferences.hasSavedProfile()) "${user.currentWeightKg} kg" else "-- kg"
        if (preferences.hasSavedGoal()) binding.edtTargetGoalWeight.setText(user.targetWeightKg.toString())
        val periods = listOf("1 tháng", "2 tháng", "3 tháng", "6 tháng", "12 tháng")
        binding.spGoalDuration.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, periods)
        val savedDuration = preferences.goalDuration()
        binding.spGoalDuration.setSelection(periods.indexOf(savedDuration).takeIf { it >= 0 } ?: 2)
        selectGoal(preferences.getGoalType())
        binding.btnBackGoalSetting.setOnClickListener { finish() }
        binding.optSpeedSlow.setOnClickListener { selectGoal("Giảm cân") }
        binding.optSpeedStandard.setOnClickListener { selectGoal("Duy trì cân nặng") }
        binding.optSpeedFast.setOnClickListener { selectGoal("Tăng cân") }
        binding.btnSaveGoalPlan.setOnClickListener {
            val weight = binding.edtTargetGoalWeight.text.toString().trim().toFloatOrNull()
            if (weight == null || weight <= 0 || weight > 350) {
                binding.edtTargetGoalWeight.error = "Vui lòng nhập cân nặng hợp lệ"
                return@setOnClickListener
            }
            val u = preferences.getUser()
            u.targetWeightKg = weight
            preferences.saveUser(u)
            preferences.saveGoalType(goalType)
            preferences.saveGoalDuration(binding.spGoalDuration.selectedItem.toString())
            Toast.makeText(this, "Đã lưu mục tiêu $goalType – $weight kg", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.navGoalTabHome.setOnClickListener { openTab(0) }
        binding.navGoalTabDiet.setOnClickListener { openTab(1) }
        binding.navGoalTabExercise.setOnClickListener { openTab(2) }
        binding.navGoalTabProfile.setOnClickListener { openTab(3) }
        binding.navGoalQuickAdd.setOnClickListener { QuickAddBottomSheet().show(supportFragmentManager, "goalQuickAdd") }
    }
    private fun selectGoal(type: String) {
        goalType = type
        val groups = listOf(binding.optSpeedSlow, binding.optSpeedStandard, binding.optSpeedFast)
        val types = listOf("Giảm cân", "Duy trì cân nặng", "Tăng cân")
        groups.forEachIndexed { index, group ->
            group.setBackgroundResource(if (types[index] == type) R.drawable.bg_fgm_selected else R.drawable.bg_fgm_option)
        }
        binding.tvGoalTip.text = when(type) {
            "Tăng cân" -> "Tăng cân từng bước, kết hợp dinh dưỡng và vận động phù hợp"
            "Duy trì cân nặng" -> "Ăn uống cân bằng, vận động đều đặn để giữ cân nặng"
            else -> "Giảm 0,5kg - 1kg mỗi tuần là an toàn và tốt cho sức khỏe"
        }
    }
    private fun openTab(index: Int) {
        startActivity(Intent(this, MainActivity::class.java).putExtra(MainActivity.EXTRA_OPEN_TAB, index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        finish()
    }
}
