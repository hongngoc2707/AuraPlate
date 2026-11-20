package com.example.auraplate.ui.goal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.MainActivity
import com.example.auraplate.adapter.WeightRecordAdapter
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.data.local.WeightHistoryStore
import com.example.auraplate.data.model.WeightRecord
import com.example.auraplate.databinding.ActivityWeightProgressBinding
import com.example.auraplate.ui.dialog.UpdateWeightDialog
import com.example.auraplate.ui.dialog.QuickAddBottomSheet
import com.example.auraplate.utils.BmiCalculator
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/** Tiến độ từ hồ sơ và lịch sử cân nặng được người dùng nhập thật. */
class WeightProgressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeightProgressBinding
    private lateinit var preferences: AppPreferences
    private val recordItems = mutableListOf<WeightRecord>()
    private lateinit var adapter: WeightRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppPreferences(this)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, bars.top, 0, bars.bottom)
            insets
        }
        supportFragmentManager.setFragmentResultListener("weight_updated", this) { _, _ ->
            loadData()
        }
        binding.rvWeightHistory.layoutManager = LinearLayoutManager(this)
        adapter = WeightRecordAdapter(recordItems)
        binding.rvWeightHistory.adapter = adapter
        setupListeners()
        loadData()
    }

    private fun loadData() {
        val user = preferences.getUser()
        val ready = preferences.hasSavedProfile()
        val hasGoal = preferences.hasSavedGoal() && user.targetWeightKg > 0f
        val start = preferences.initialWeightKg()
        val history = WeightHistoryStore.getRecords(this)
        recordItems.clear()
        recordItems.addAll(history.asReversed())
        adapter.notifyDataSetChanged()
        binding.tvWeightHistoryEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        binding.tvWeightInitial.text = start?.let { displayKg(it) } ?: "-- kg"
        binding.tvWeightInitialDate.text = preferences.initialWeightDate().ifBlank { "Chưa ghi nhận" }
        binding.tvWeightProgressCurrent.text = if (ready) displayKg(user.currentWeightKg) else "-- kg"
        binding.tvWeightCurrentDate.text = history.lastOrNull()?.date ?: "Chưa cập nhật"
        binding.tvWeightGoal.text = if (hasGoal) displayKg(user.targetWeightKg) else "-- kg"
        binding.tvWeightGoalDate.text = if (hasGoal) preferences.goalDuration().ifBlank { "Đã thiết lập" } else "Chưa đặt mục tiêu"
        binding.tvBmiBadgeProgress.text = if (ready && user.heightCm > 0f) {
            "BMI: %.1f".format(Locale("vi", "VN"),
                BmiCalculator.calculateBmi(user.currentWeightKg, user.heightCm))
        } else "BMI: --"

        var progress = 0
        if (!ready || !hasGoal || start == null) {
            binding.tvCompletionPercent.text = "--%"
            binding.tvWeightDeltaSummary.text = "Lưu hồ sơ và mục tiêu để theo dõi tiến độ cân nặng."
        } else if (preferences.getGoalType() == "Duy trì cân nặng") {
            binding.tvCompletionPercent.text = "Đang theo dõi"
            binding.tvWeightDeltaSummary.text =
                "Chênh lệch so với mức cần duy trì: ${displayKg(abs(user.currentWeightKg - user.targetWeightKg))}."
        } else {
            val total = abs(user.targetWeightKg - start)
            if (total < 0.1f) {
                progress = 100
            } else if ((user.currentWeightKg - start) * (user.targetWeightKg - start) >= 0f) {
                progress = ((abs(user.currentWeightKg - start) / total) * 100f)
                    .roundToInt().coerceIn(0, 100)
            }
            binding.tvCompletionPercent.text = "$progress%"
            binding.tvWeightDeltaSummary.text =
                "Bắt đầu ${displayKg(start)} · Hiện tại ${displayKg(user.currentWeightKg)} · " +
                "Mục tiêu ${displayKg(user.targetWeightKg)}"
        }
        binding.pbWeightGoal.progress = progress
        binding.weightTrendChart.show(history, if (hasGoal) user.targetWeightKg else null)
    }

    private fun displayKg(value: Float): String = "%.1f kg".format(Locale("vi", "VN"), value)

    private fun setupListeners() {
        binding.btnBackWeightProgress.setOnClickListener { finish() }
        binding.navHome.setOnClickListener { openMainTab(0) }
        binding.navMeals.setOnClickListener { openMainTab(1) }
        binding.navWorkout.setOnClickListener { openMainTab(2) }
        binding.navProfile.setOnClickListener { openMainTab(3) }
        binding.navGoalQuickAdd.setOnClickListener {
            QuickAddBottomSheet().show(supportFragmentManager, "WeightQuickAdd")
        }
        binding.btnOpenUpdateWeightDialog.setOnClickListener {
            UpdateWeightDialog().show(supportFragmentManager, "UpdateWeightDialog")
        }
        binding.btnOpenReview.setOnClickListener {
            startActivity(Intent(this, GoalEvaluationActivity::class.java))
        }
        binding.cardLinkToGoalEvaluation.setOnClickListener {
            startActivity(Intent(this, GoalEvaluationActivity::class.java))
        }
    }

    private fun openMainTab(index: Int) {
        startActivity(Intent(this, MainActivity::class.java)
            .putExtra(MainActivity.EXTRA_OPEN_TAB, index)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) loadData()
    }
}
