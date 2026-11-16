package com.example.auraplate.data.model

import java.io.Serializable

data class User(
    var name: String = "Nguyễn Văn An",
    var email: String = "an.nguyen@auraplate.vn",
    var age: Int = 22,
    var gender: String = "Nam",
    var heightCm: Float = 172.0f,
    var currentWeightKg: Float = 57.4f,
    var targetWeightKg: Float = 56.0f
) : Serializable
