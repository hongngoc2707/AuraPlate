package com.example.auraplate.data.model

import java.io.Serializable

data class ExerciseItem(
    val id: String = "",
    val name: String = "",
    val durationMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val category: String = "",
    val timeRecorded: String = ""
) : Serializable
