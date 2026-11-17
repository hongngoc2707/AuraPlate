package com.example.auraplate.data.model

import java.io.Serializable

data class FoodItem(
    val id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val portion: String = "",
    val carbsGrams: Float = 0f,
    val proteinGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val category: String = "",
    var mealType: String = ""
) : Serializable
