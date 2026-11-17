package com.example.auraplate.ui.goal

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.auraplate.R
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityGoalSettingBinding

class GoalSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSettingBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        loadData()
        setupListeners()
    }

    private fun loadData() {
        val user = preferences.getUser()
        binding.tvCurrentGoalWeight.text = "${user.currentWeightKg} kg"
        binding.edtTargetGoalWeight.setText(user.targetWeightKg.toString())
    }

    private fun setupListeners() {
        binding.btnBackGoalSetting.setOnClickListener { finish() }

        binding.optSpeedSlow.setOnClickListener { selectSpeed(binding.optSpeedSlow) }
        binding.optSpeedStandard.setOnClickListener { selectSpeed(binding.optSpeedStandard) }
        binding.optSpeedFast.setOnClickListener { selectSpeed(binding.optSpeedFast) }

        binding.btnSaveGoalPlan.setOnClickListener {
            try {
                val targetWeight = binding.edtTargetGoalWeight.text.toString().trim().toFloat()
                val user = preferences.getUser()
                user.targetWeightKg = targetWeight
                preferences.saveUser(user)

                Toast.makeText(this, "🎯 Đã lưu kế hoạch mục tiêu: $targetWeight kg!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Vui lòng nhập cân nặng hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectSpeed(activeLayout: LinearLayout) {
        val layouts = arrayOf(binding.optSpeedSlow, binding.optSpeedStandard, binding.optSpeedFast)
        for (l in layouts) {
            val title = l.getChildAt(0) as TextView
            val sub = l.getChildAt(1) as TextView
            if (l == activeLayout) {
                l.setBackgroundResource(R.drawable.bg_segment_active)
                title.setTextColor(ContextCompat.getColor(this, R.color.white))
                sub.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                l.setBackgroundResource(R.drawable.bg_rounded_card)
                title.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                sub.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }
}
