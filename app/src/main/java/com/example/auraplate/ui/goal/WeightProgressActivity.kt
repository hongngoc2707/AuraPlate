package com.example.auraplate.ui.goal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraplate.adapter.WeightRecordAdapter
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.ActivityWeightProgressBinding
import com.example.auraplate.ui.dialog.UpdateWeightDialog
import com.example.auraplate.utils.BmiCalculator

class WeightProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeightProgressBinding
    private lateinit var preferences: AppPreferences
    private lateinit var repository: MockDataRepository
    private var adapter: WeightRecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        repository = MockDataRepository.getInstance()

        setupRecyclerView()
        loadData()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvWeightHistory.layoutManager = LinearLayoutManager(this)
        val records = repository.getWeightHistory()
        adapter = WeightRecordAdapter(records)
        binding.rvWeightHistory.adapter = adapter
    }

    private fun loadData() {
        val user = preferences.getUser()
        binding.tvWeightProgressCurrent.text = "${user.currentWeightKg} kg"

        val bmi = BmiCalculator.calculateBmi(user.currentWeightKg, user.heightCm)
        binding.tvBmiBadgeProgress.text = "BMI ${String.format("%.1f", bmi)} • ${BmiCalculator.getBmiStatus(bmi).uppercase()}"
    }

    private fun setupListeners() {
        binding.btnBackWeightProgress.setOnClickListener { finish() }

        binding.btnOpenUpdateWeightDialog.setOnClickListener {
            val dialog = UpdateWeightDialog()
            dialog.show(supportFragmentManager, "UpdateWeightDialog")
        }

        // Bấm vào Card để sang màn hình 12
        binding.cardLinkToGoalEvaluation.setOnClickListener {
            val intent = Intent(this, GoalEvaluationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
        adapter?.notifyDataSetChanged()
    }
}
