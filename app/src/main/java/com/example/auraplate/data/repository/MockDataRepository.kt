package com.example.auraplate.data.repository

import com.example.auraplate.data.model.ExerciseItem
import com.example.auraplate.data.model.FoodItem
import com.example.auraplate.data.model.WeightRecord

class MockDataRepository private constructor() {

    private val foodDatabase = mutableListOf<FoodItem>()
    private val todayLoggedFoods = mutableListOf<FoodItem>()
    private val exerciseDatabase = mutableListOf<ExerciseItem>()
    private val todayLoggedExercises = mutableListOf<ExerciseItem>()
    private val weightHistory = mutableListOf<WeightRecord>()

    init {
        seedFoodDatabase()
        seedTodayFoods()
        seedExerciseDatabase()
        seedTodayExercises()
        seedWeightHistory()
    }

    companion object {
        @Volatile
        private var instance: MockDataRepository? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): MockDataRepository {
            return instance ?: MockDataRepository().also { instance = it }
        }
    }

    private fun seedFoodDatabase() {
        foodDatabase.add(FoodItem("F1", "Phở bò tái nạm", 450, "1 tô vừa (450g)", 58f, 26f, 12f, "Món nước", "Bữa sáng"))
        foodDatabase.add(FoodItem("F2", "Bún chả Hà Nội", 520, "1 suất tiêu chuẩn", 62f, 24f, 18f, "Món nước", "Bữa trưa"))
        foodDatabase.add(FoodItem("F3", "Cơm tấm sườn nướng", 580, "1 dĩa (1 miếng sườn)", 68f, 28f, 22f, "Cơm / Mì", "Bữa trưa"))
        foodDatabase.add(FoodItem("F4", "Ức gà áp chảo", 220, "1 phần (150g)", 0f, 38f, 5f, "Thịt / Cá", "Bữa tối"))
        foodDatabase.add(FoodItem("F5", "Salad rau trộn dầu giấm", 110, "1 dĩa (150g)", 12f, 2f, 6f, "Rau củ", "Bữa tối"))
        foodDatabase.add(FoodItem("F6", "Trứng gà luộc", 78, "1 quả (50g)", 0.6f, 6.3f, 5.3f, "Món phụ", "Bữa sáng"))
        foodDatabase.add(FoodItem("F7", "Bánh mì ốp la", 380, "1 ổ (2 trứng)", 45f, 14f, 15f, "Bánh mì", "Bữa sáng"))
        foodDatabase.add(FoodItem("F8", "Chuối tiêu chín", 90, "1 quả (100g)", 23f, 1.1f, 0.3f, "Trái cây", "Bữa phụ"))
        foodDatabase.add(FoodItem("F9", "Sữa chua Hy Lạp không đường", 95, "1 hộp (100g)", 4f, 10f, 3f, "Món phụ", "Bữa phụ"))
        foodDatabase.add(FoodItem("F10", "Cá hồi áp chảo măng tây", 320, "1 phần (180g)", 6f, 32f, 18f, "Thịt / Cá", "Bữa tối"))
    }

    private fun seedTodayFoods() {
        todayLoggedFoods.add(FoodItem("T1", "Bánh mì ốp la", 380, "1 ổ", 45f, 14f, 15f, "Bánh mì", "Bữa sáng"))
        todayLoggedFoods.add(FoodItem("T2", "Cơm gạo lứt thịt nạc luộc", 460, "1 phần", 55f, 32f, 10f, "Cơm / Mì", "Bữa trưa"))
        todayLoggedFoods.add(FoodItem("T3", "Ức gà xé xà lách", 280, "1 dĩa lớn", 12f, 35f, 7f, "Rau củ", "Bữa tối"))
        todayLoggedFoods.add(FoodItem("T4", "Táo xanh & Sữa chua", 140, "1 phần", 22f, 6f, 2f, "Trái cây", "Bữa phụ"))
    }

    private fun seedExerciseDatabase() {
        exerciseDatabase.add(ExerciseItem("E1", "Chạy bộ ngoài trời", 30, 240, "Cardio", "Sáng"))
        exerciseDatabase.add(ExerciseItem("E2", "Đạp xe thể thao", 30, 180, "Cardio", "Chiều"))
        exerciseDatabase.add(ExerciseItem("E3", "Tập gym (Kháng lực)", 45, 220, "Thể hình", "Chiều"))
        exerciseDatabase.add(ExerciseItem("E4", "Bơi lội tự do", 30, 260, "Toàn thân", "Sáng"))
        exerciseDatabase.add(ExerciseItem("E5", "Nhảy dây nhanh", 15, 160, "Cardio", "Tối"))
        exerciseDatabase.add(ExerciseItem("E6", "Yoga & Giãn cơ", 30, 90, "Thư giãn", "Tối"))
    }

    private fun seedTodayExercises() {
        todayLoggedExercises.add(ExerciseItem("TE1", "Chạy bộ ngoài trời", 25, 210, "Cardio", "06:30"))
        todayLoggedExercises.add(ExerciseItem("TE2", "Đạp xe thư giãn", 17, 170, "Cardio", "17:45"))
    }

    private fun seedWeightHistory() {
        weightHistory.add(WeightRecord("01/11", 60.0f, 23.8f, "Khởi đầu lộ trình"))
        weightHistory.add(WeightRecord("05/11", 59.4f, 23.5f, "Tuần 1: Giảm tốt"))
        weightHistory.add(WeightRecord("09/11", 58.7f, 23.2f, "Tuần 2: Duy trì ăn sạch"))
        weightHistory.add(WeightRecord("13/11", 58.0f, 23.0f, "Tuần 3: Tăng cường tập"))
        weightHistory.add(WeightRecord("Hôm nay", 57.4f, 22.8f, "Đạt chuẩn thể trạng"))
    }

    fun getFoodDatabase(): List<FoodItem> = foodDatabase
    fun getTodayLoggedFoods(): List<FoodItem> = todayLoggedFoods
    fun addLoggedFood(item: FoodItem) { todayLoggedFoods.add(item) }

    fun getExerciseDatabase(): List<ExerciseItem> = exerciseDatabase
    fun getTodayLoggedExercises(): List<ExerciseItem> = todayLoggedExercises
    fun addLoggedExercise(item: ExerciseItem) { todayLoggedExercises.add(item) }

    fun getWeightHistory(): List<WeightRecord> = weightHistory
    fun addWeightRecord(record: WeightRecord) { weightHistory.add(record) }

    fun getTodayTotalCaloriesIn(): Int = todayLoggedFoods.sumOf { it.calories }

    fun getTodayTotalCaloriesBurned(): Int = todayLoggedExercises.sumOf { it.caloriesBurned }
}
