package com.example.auraplate.ui.exercise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.auraplate.R
import com.example.auraplate.data.model.ExerciseItem
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.ActivityAddExerciseBinding
import com.example.auraplate.ui.dialog.OtherActivityDialog
import com.example.auraplate.utils.DateFormatter

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExerciseBinding
    private var selectedSportName = "Chạy bộ ngoài trời"
    private var calPerMinute = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateEstimatedBurn()
    }

    private fun setupListeners() {
        binding.btnBackAddExercise.setOnClickListener { finish() }

        binding.optRun.setOnClickListener { selectSport("Chạy bộ ngoài trời", 8, binding.optRun) }
        binding.optCycling.setOnClickListener { selectSport("Đạp xe thể thao", 6, binding.optCycling) }
        binding.optGym.setOnClickListener { selectSport("Tập Gym (Kháng lực)", 5, binding.optGym) }
        binding.optSwim.setOnClickListener { selectSport("Bơi lội tự do", 9, binding.optSwim) }
        binding.optRope.setOnClickListener { selectSport("Nhảy dây nhanh", 11, binding.optRope) }
        binding.optYoga.setOnClickListener { selectSport("Yoga & Giãn cơ", 3, binding.optYoga) }

        binding.btnOpenOtherActivityDialog.setOnClickListener {
            val dialog = OtherActivityDialog()
            dialog.show(supportFragmentManager, "OtherActivityDialog")
        }

        binding.chip15Min.setOnClickListener { setDuration(15, binding.chip15Min) }
        binding.chip30Min.setOnClickListener { setDuration(30, binding.chip30Min) }
        binding.chip45Min.setOnClickListener { setDuration(45, binding.chip45Min) }
        binding.chip60Min.setOnClickListener { setDuration(60, binding.chip60Min) }

        binding.edtExerciseDuration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                updateEstimatedBurn()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSaveExercise.setOnClickListener {
            try {
                val duration = binding.edtExerciseDuration.text.toString().trim().toInt()
                val burned = duration * calPerMinute
                val time = DateFormatter.getCurrentTime()

                val item = ExerciseItem(
                    id = "E_${System.currentTimeMillis()}",
                    name = selectedSportName,
                    durationMinutes = duration,
                    caloriesBurned = burned,
                    category = "Vận động",
                    timeRecorded = time
                )
                MockDataRepository.getInstance().addLoggedExercise(item)

                Toast.makeText(
                    this,
                    "🔥 Đã lưu: $selectedSportName ($duration phút, -$burned kcal)",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Vui lòng nhập thời gian hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectSport(name: String, calPerMin: Int, activeView: TextView) {
        selectedSportName = name
        calPerMinute = calPerMin
        val sports = arrayOf(binding.optRun, binding.optCycling, binding.optGym, binding.optSwim, binding.optRope, binding.optYoga)
        for (s in sports) {
            if (s == activeView) {
                s.setBackgroundResource(R.drawable.bg_segment_active)
                s.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                s.setBackgroundResource(R.drawable.bg_rounded_card)
                s.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            }
        }
        updateEstimatedBurn()
    }

    private fun setDuration(min: Int, activeChip: TextView) {
        binding.edtExerciseDuration.setText(min.toString())
        val chips = arrayOf(binding.chip15Min, binding.chip30Min, binding.chip45Min, binding.chip60Min)
        for (c in chips) {
            if (c == activeChip) {
                c.setBackgroundResource(R.drawable.bg_segment_active)
                c.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                c.setBackgroundResource(R.drawable.bg_pill_badge)
                c.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
        updateEstimatedBurn()
    }

    private fun updateEstimatedBurn() {
        try {
            val duration = binding.edtExerciseDuration.text.toString().trim().toInt()
            val burned = duration * calPerMinute
            binding.tvEstimatedBurn.text = "$burned kcal"
        } catch (e: Exception) {
            binding.tvEstimatedBurn.text = "0 kcal"
        }
    }
}
