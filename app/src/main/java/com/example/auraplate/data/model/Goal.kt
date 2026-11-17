package com.example.auraplate.data.model

import java.io.Serializable
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class Goal(
    var startWeightKg: Float = 60.0f,
    var currentWeightKg: Float = 57.4f,
    var targetWeightKg: Float = 56.0f,
    var targetDate: String = "15/12/2026",
    var dailyCalorieTarget: Int = 1700,
    var dailyBurnTarget: Int = 300,
    var carbsRatio: Int = 48,
    var proteinRatio: Int = 28,
    var fatRatio: Int = 24,
    var healthScore: Int = 88
) : Serializable {

    fun getWeightLostKg(): Float = max(0f, startWeightKg - currentWeightKg)

    fun getWeightRemainingKg(): Float = max(0f, currentWeightKg - targetWeightKg)

    fun getCompletionPercent(): Int {
        val totalDiff = startWeightKg - targetWeightKg
        if (totalDiff <= 0) return 100
        val progress = (startWeightKg - currentWeightKg) / totalDiff
        return min(100, max(0, (progress * 100).roundToInt()))
    }
}
