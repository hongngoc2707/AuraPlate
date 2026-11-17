package com.example.auraplate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.auraplate.data.model.User
import kotlin.math.min

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AuraPlatePrefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_AGE = "user_age"
        private const val KEY_USER_GENDER = "user_gender"
        private const val KEY_HEIGHT = "user_height"
        private const val KEY_WEIGHT = "user_weight"
        private const val KEY_TARGET_WEIGHT = "target_weight"
        private const val KEY_WATER_ML = "water_intake_ml"
        private const val KEY_STREAK_DAYS = "streak_days"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    fun getUser(): User {
        return User(
            name = prefs.getString(KEY_USER_NAME, "Nguyễn Văn An") ?: "Nguyễn Văn An",
            email = prefs.getString(KEY_USER_EMAIL, "an.nguyen@auraplate.vn") ?: "an.nguyen@auraplate.vn",
            age = prefs.getInt(KEY_USER_AGE, 22),
            gender = prefs.getString(KEY_USER_GENDER, "Nam") ?: "Nam",
            heightCm = prefs.getFloat(KEY_HEIGHT, 172.0f),
            currentWeightKg = prefs.getFloat(KEY_WEIGHT, 57.4f),
            targetWeightKg = prefs.getFloat(KEY_TARGET_WEIGHT, 56.0f)
        )
    }

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putInt(KEY_USER_AGE, user.age)
            .putString(KEY_USER_GENDER, user.gender)
            .putFloat(KEY_HEIGHT, user.heightCm)
            .putFloat(KEY_WEIGHT, user.currentWeightKg)
            .putFloat(KEY_TARGET_WEIGHT, user.targetWeightKg)
            .apply()
    }

    fun getWaterIntakeMl(): Int = prefs.getInt(KEY_WATER_ML, 1500)

    fun addWater(amountMl: Int) {
        val current = getWaterIntakeMl()
        prefs.edit().putInt(KEY_WATER_ML, min(4000, current + amountMl)).apply()
    }

    fun getStreakDays(): Int = prefs.getInt(KEY_STREAK_DAYS, 6)
}
