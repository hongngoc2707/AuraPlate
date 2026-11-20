package com.example.auraplate.ui.exercise

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.R
import com.example.auraplate.ui.dialog.OtherActivityDialog
import com.example.auraplate.ui.linh.LinhDataStore
import com.example.auraplate.ui.linh.LinhExercise
import kotlin.math.roundToInt
import com.example.auraplate.utils.applySystemBars

/** 10_ThemVanDong: chọn một loại, thời gian, cường độ; ước tính kcal và lưu. */
class AddExerciseActivity : AppCompatActivity() {
    companion object { const val EXTRA_DATE = "exercise_date" }

    private val types = linkedMapOf(
        R.id.exerciseCardWalk to "Đi bộ", R.id.exerciseCardRun to "Chạy bộ",
        R.id.exerciseCardYoga to "Yoga", R.id.exerciseCardGym to "Tập gym",
        R.id.exerciseCardBike to "Đạp xe", R.id.exerciseCardDance to "Nhảy múa",
        R.id.exerciseCardSwim to "Bơi lội", R.id.exerciseCardOther to "Khác"
    )
    private var selectedName = "Đi bộ"
    private var selectedTile = R.id.exerciseCardWalk
    private var date = LinhDataStore.todayKey()
    private val durationChoices = listOf(15, 30, 45, 60, 90, 120)
    private val intensities = listOf("Nhẹ", "Vừa phải", "Mạnh")
    private var weightKg = 0.0
    private var demoWeight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)
        applySystemBars(findViewById(android.R.id.content))
        date = intent.getStringExtra(EXTRA_DATE) ?: LinhDataStore.todayKey()
        findViewById<View>(R.id.exerciseAddBack).setOnClickListener { finish() }

        // Connect to the shared profile in the existing AuraPlate project.
        val preferences = com.example.auraplate.data.local.AppPreferences(this)
        if (preferences.hasSavedProfile()) {
            weightKg = preferences.getUser().currentWeightKg.toDouble()
            demoWeight = false
        }
        findViewById<TextView>(R.id.exerciseWeightValue).text =
            if (demoWeight) "-- kg (chưa có hồ sơ)" else "%.1f kg".format(weightKg)

        types.forEach { (viewId, name) ->
            findViewById<View>(viewId).setOnClickListener {
                if (name == "Khác") {
                    OtherActivityDialog().show(supportFragmentManager, "other_activity")
                } else {
                    selectedName = name; selectedTile = viewId
                    updateTileStyle(); updateCalories()
                }
            }
        }
        supportFragmentManager.setFragmentResultListener(LinhDataStore.RESULT_OTHER_ACTIVITY, this) { _, bundle ->
            val name = bundle.getString("exercise_name")?.trim().orEmpty()
            if (name.isNotEmpty()) {
                selectedName = name; selectedTile = R.id.exerciseCardOther
                updateTileStyle(); updateCalories()
            }
        }

        val durations = findViewById<Spinner>(R.id.exerciseDurationSpinner)
        durations.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            durationChoices.map { "$it phút" })
        durations.setSelection(1) // 30 phút
        val intensity = findViewById<Spinner>(R.id.exerciseIntensitySpinner)
        intensity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, intensities)
        intensity.setSelection(1) // Vừa phải
        val onSelect = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCalories()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        durations.onItemSelectedListener = onSelect
        intensity.onItemSelectedListener = onSelect
        findViewById<View>(R.id.exerciseSave).setOnClickListener {
            if (demoWeight) {
                Toast.makeText(this, "Hãy thiết lập cân nặng trong hồ sơ trước", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val minutes = selectedMinutes()
            val kcal = calculatedKcal()
            LinhDataStore.saveExercise(this, LinhExercise(date, selectedName,
                minutes, selectedIntensity(), kcal))
            Toast.makeText(this, "Đã lưu hoạt động $selectedName", Toast.LENGTH_SHORT).show()
            finish()
        }
        updateTileStyle()
        updateCalories()
    }

    private fun updateTileStyle() {
        types.keys.forEach { id ->
            findViewById<View>(id).setBackgroundResource(
                if (id == selectedTile) R.drawable.bg_fgm_selected else R.drawable.bg_fgm_card
            )
        }
        findViewById<TextView>(R.id.exerciseCardOtherLabel).text =
            if (selectedTile == R.id.exerciseCardOther) selectedName.take(12) else "Khác"
    }
    private fun selectedMinutes(): Int {
        val i = findViewById<Spinner>(R.id.exerciseDurationSpinner).selectedItemPosition
        return durationChoices.getOrElse(i) { 30 }
    }
    private fun selectedIntensity(): String {
        val i = findViewById<Spinner>(R.id.exerciseIntensitySpinner).selectedItemPosition
        return intensities.getOrElse(i) { "Vừa phải" }
    }

    private fun calculatedKcal(): Int {
        // MET tham khảo cho demo giao diện; chưa phải khuyến nghị dinh dưỡng chính xác.
        val met = when (selectedName) {
            "Đi bộ" -> 3.5; "Chạy bộ" -> 8.3; "Yoga" -> 2.5
            "Tập gym" -> 5.0; "Đạp xe" -> 6.0; "Nhảy múa" -> 4.5
            "Bơi lội" -> 6.0; else -> 3.5
        }
        val factor = when (selectedIntensity()) {
            "Nhẹ" -> 0.8; "Mạnh" -> 1.3; else -> 1.0
        }
        return (met * factor * weightKg * selectedMinutes() / 60.0).roundToInt()
    }
    private fun updateCalories() {
        findViewById<TextView>(R.id.exerciseCaloriesEstimate).text =
            if (demoWeight) "-- kcal (cần cân nặng)" else "${calculatedKcal()} kcal"
    }
}
