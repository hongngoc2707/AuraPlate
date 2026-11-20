package com.example.auraplate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.auraplate.data.model.User
import kotlin.math.min

class AppPreferences(context: Context) {
    private val prefsContext = context.applicationContext
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
        private const val KEY_PROFILE_CONFIRMED = "profile_confirmed"
        private const val KEY_GOAL_CONFIRMED = "goal_confirmed"
        private const val KEY_INITIAL_WEIGHT = "initial_weight_kg"
        private const val KEY_INITIAL_WEIGHT_DATE = "initial_weight_date"
        private const val KEY_GOAL_DURATION = "goal_duration"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    fun getUser(): User {
        return User(
            name = prefs.getString(KEY_USER_NAME, "bạn") ?: "bạn",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            age = prefs.getInt(KEY_USER_AGE, 0),
            gender = prefs.getString(KEY_USER_GENDER, "Nam") ?: "Nam",
            heightCm = prefs.getFloat(KEY_HEIGHT, 0f),
            currentWeightKg = prefs.getFloat(KEY_WEIGHT, 0f),
            targetWeightKg = prefs.getFloat(KEY_TARGET_WEIGHT, 0f)
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

    /** Chỉ hiện chỉ số cá nhân khi người dùng đã chủ động lưu hồ sơ. */
    fun hasSavedProfile(): Boolean = prefs.getBoolean(KEY_PROFILE_CONFIRMED, false)

    /** Ghi nhận cân nặng ban đầu duy nhất khi hoàn thành hồ sơ lần đầu. */
    fun markProfileCompleted(weightKg: Float, heightCm: Float) {
        if (!hasSavedProfile() && weightKg > 0f && heightCm > 0f) {
            val today = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("vi", "VN"))
                .format(java.util.Date())
            prefs.edit()
                .putFloat(KEY_INITIAL_WEIGHT, weightKg)
                .putString(KEY_INITIAL_WEIGHT_DATE, today)
                .apply()
            val bmi = com.example.auraplate.utils.BmiCalculator.calculateBmi(weightKg, heightCm)
            WeightHistoryStore.addRecordIfDateAbsent(prefsContext,
                com.example.auraplate.data.model.WeightRecord(today, weightKg, bmi, "Thiết lập hồ sơ"))
        }
        prefs.edit().putBoolean(KEY_PROFILE_CONFIRMED, true).apply()
    }

    fun initialWeightKg(): Float? =
        if (prefs.contains(KEY_INITIAL_WEIGHT)) prefs.getFloat(KEY_INITIAL_WEIGHT, 0f).takeIf { it > 0f } else null

    fun initialWeightDate(): String = prefs.getString(KEY_INITIAL_WEIGHT_DATE, "") ?: ""

    fun hasSavedGoal(): Boolean = prefs.getBoolean(KEY_GOAL_CONFIRMED, false)

    fun goalDuration(): String = prefs.getString(KEY_GOAL_DURATION, "") ?: ""

    fun saveGoalDuration(value: String) = prefs.edit().putString(KEY_GOAL_DURATION, value).apply()

    /** Mức độ vận động dùng chung cho Thiết lập và Chỉnh sửa hồ sơ. */
    fun getActivityLevel(): String =
        prefs.getString("user_activity_level", "Vận động nhẹ") ?: "Vận động nhẹ"

    fun saveActivityLevel(value: String) {
        prefs.edit().putString("user_activity_level", value).apply()
    }

    fun getGoalType(): String = prefs.getString("goal_type", "Giảm cân") ?: "Giảm cân"

    fun saveGoalType(goalType: String) {
        prefs.edit().putString("goal_type", goalType).putBoolean(KEY_GOAL_CONFIRMED, true).apply()
    }

    fun getWaterIntakeMl(): Int = prefs.getInt(KEY_WATER_ML, 0)

    fun addWater(amountMl: Int) {
        val current = getWaterIntakeMl()
        prefs.edit().putInt(KEY_WATER_ML, min(4000, current + amountMl)).apply()
    }

    fun getStreakDays(): Int = prefs.getInt(KEY_STREAK_DAYS, 0)
}
