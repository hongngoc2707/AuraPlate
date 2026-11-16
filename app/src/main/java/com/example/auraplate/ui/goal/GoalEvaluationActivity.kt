package com.example.auraplate.ui.goal

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.auraplate.R
import com.example.auraplate.databinding.ActivityGoalEvaluationBinding
import com.example.auraplate.ui.dialog.QuickAddBottomSheet

class GoalEvaluationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalEvaluationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalEvaluationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack?.setOnClickListener { finish() }

        binding.tabWeek?.setOnClickListener { selectTab(0) }
        binding.tabMonth?.setOnClickListener { selectTab(1) }
        binding.tabAll?.setOnClickListener { selectTab(2) }

        binding.btnAdjustGoal?.setOnClickListener {
            val intent = Intent(this, GoalSettingActivity::class.java)
            startActivity(intent)
        }

        binding.btnExportReport?.setOnClickListener {
            Toast.makeText(this, "📄 Đang xuất Báo cáo 3 (Đánh giá tiến độ dinh dưỡng)... Hoàn tất!", Toast.LENGTH_LONG).show()
        }

        // Bottom Navigation in Screen 12
        binding.navGoalHome?.setOnClickListener { finish() }
        binding.navGoalDiet?.setOnClickListener { finish() }
        binding.fabGoalQuickAdd?.setOnClickListener {
            val sheet = QuickAddBottomSheet()
            sheet.show(supportFragmentManager, "QuickAddBottomSheet")
        }
        binding.navGoalExercise?.setOnClickListener { finish() }
    }

    private fun selectTab(index: Int) {
        val tabs = arrayOf(binding.tabWeek, binding.tabMonth, binding.tabAll)
        for (i in tabs.indices) {
            val tab = tabs[i] ?: continue
            if (i == index) {
                tab.setBackgroundResource(R.drawable.bg_segment_active)
                tab.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                tab.background = null
                tab.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }
}
