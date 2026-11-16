package com.example.auraplate.utils

import kotlin.math.roundToInt

object BmiCalculator {
    @JvmStatic
    fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0f) return 0f
        val heightM = heightCm / 100.0f
        return ((weightKg / (heightM * heightM)) * 10.0f).roundToInt() / 10.0f
    }

    @JvmStatic
    fun getBmiStatus(bmi: Float): String {
        return when {
            bmi < 18.5f -> "Thiếu cân"
            bmi < 23.0f -> "Đạt chuẩn"
            bmi < 25.0f -> "Thừa cân"
            else -> "Béo phì"
        }
    }

    @JvmStatic
    fun calculateBmr(weightKg: Float, heightCm: Float, age: Int, gender: String): Int {
        return if ("Nữ".equals(gender, ignoreCase = true)) {
            (655 + (9.6 * weightKg) + (1.8 * heightCm) - (4.7 * age)).toInt()
        } else {
            (66 + (13.7 * weightKg) + (5.0 * heightCm) - (6.8 * age)).toInt()
        }
    }
}
