package com.example.auraplate.data.model

import java.io.Serializable

data class WeightRecord(
    val date: String = "",
    val weightKg: Float = 0f,
    val bmi: Float = 0f,
    val note: String = ""
) : Serializable
