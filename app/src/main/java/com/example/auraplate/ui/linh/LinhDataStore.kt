package com.example.auraplate.ui.linh

import android.content.Context
import com.example.auraplate.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Dữ liệu mẫu dùng chung cho 6 giao diện Linh. Có thể thay bằng Room khi nhóm ghép dữ liệu. */
data class LinhFood(val id: String, val name: String, val kcal: Int, val unit: String, val icon: String)
data class LinhMeal(val date: String, val mealType: String, val foodId: String, val name: String,
                    val quantity: Int, val kcal: Int)
data class LinhExercise(val date: String, val name: String, val minutes: Int,
                        val intensity: String, val kcal: Int)

object LinhDataStore {
    const val BREAKFAST = "Sáng"
    const val LUNCH = "Trưa"
    const val DINNER = "Tối"
    const val SNACK = "Phụ"
    const val RESULT_FOOD = "linh_result_food"
    const val RESULT_OTHER_ACTIVITY = "linh_result_other_activity"

    val foods = listOf(
        LinhFood("rice", "Cơm trắng", 130, "100 g", "🍚"),
        LinhFood("egg", "Trứng luộc", 78, "1 quả", "🥚"),
        LinhFood("broccoli", "Bông cải xanh", 34, "100 g", "🥦"),
        LinhFood("sweet_potato", "Khoai lang", 86, "100 g", "🍠"),
        LinhFood("chicken", "Ức gà nướng", 165, "100 g", "🍗"),
        LinhFood("avocado", "Bơ", 160, "100 g", "🥑"),
        LinhFood("bread", "Bánh mì", 265, "100 g", "🍞"),
        LinhFood("milk", "Sữa tươi", 61, "100 ml", "🥛"),
        LinhFood("salmon", "Cá hồi", 208, "100 g", "🐟"),
        LinhFood("apple", "Táo", 52, "100 g", "🍎"),
        LinhFood("banana", "Chuối", 89, "100 g", "🍌"),
        LinhFood("oat", "Yến mạch đã nấu", 71, "100 g", "🥣"),
        LinhFood("yogurt", "Sữa chua", 60, "100 g", "🥛")
    )

    fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    fun showDate(key: String): String = try {
        val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key)!!
        SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(d)
    } catch (_: Exception) { key }

    fun foodById(id: String): LinhFood? = foods.firstOrNull { it.id == id }
    fun foodImage(id: String): Int = when (id) {
        "rice" -> R.drawable.ap_rice
        "egg" -> R.drawable.ap_egg
        "broccoli" -> R.drawable.ap_broccoli
        "sweet_potato" -> R.drawable.ap_sweet_potato
        "chicken" -> R.drawable.ap_chicken
        "avocado" -> R.drawable.ap_avocado
        "bread" -> R.drawable.ap_bread
        "milk" -> R.drawable.ap_milk
        "salmon" -> R.drawable.ap_salmon
        "apple" -> R.drawable.ap_apple
        "banana" -> R.drawable.ap_banana
        "oat" -> R.drawable.ap_rice
        "yogurt" -> R.drawable.ap_yogurt
        else -> R.drawable.ap_rice
    }


    private fun read(context: Context, key: String): JSONArray {
        val raw = context.getSharedPreferences("auraplate_linh_demo", Context.MODE_PRIVATE)
            .getString(key, "[]") ?: "[]"
        return try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
    }
    private fun write(context: Context, key: String, data: JSONArray) {
        context.getSharedPreferences("auraplate_linh_demo", Context.MODE_PRIVATE)
            .edit().putString(key, data.toString()).apply()
    }

    fun saveMeal(context: Context, date: String, type: String, items: Map<String, Int>) {
        val array = read(context, "meals")
        items.forEach { (id, quantity) ->
            val f = foodById(id) ?: return@forEach
            if (quantity > 0) {
                array.put(JSONObject().apply {
                    put("date", date); put("type", type); put("foodId", id)
                    put("name", f.name); put("quantity", quantity)
                    put("kcal", f.kcal * quantity)
                })
            }
        }
        write(context, "meals", array)
    }

    /** Tất cả bữa đã lưu, dùng tổng hợp tuần/tháng và báo cáo tiến độ. */
    fun allMeals(context: Context): List<LinhMeal> {
        val saved = read(context, "meals")
        return (0 until saved.length()).mapNotNull { index ->
            val record = saved.optJSONObject(index) ?: return@mapNotNull null
            LinhMeal(
                date = record.optString("date"),
                mealType = record.optString("type"),
                foodId = record.optString("foodId"),
                name = record.optString("name"),
                quantity = record.optInt("quantity"),
                kcal = record.optInt("kcal")
            )
        }
    }

    /** Nhật ký ăn uống cho một ngày được chọn. */
    fun mealsFor(context: Context, date: String): List<LinhMeal> =
        allMeals(context).filter { it.date == date }

    fun saveExercise(context: Context, exercise: LinhExercise) {
        val arr = read(context, "exercises")
        arr.put(JSONObject().apply {
            put("date", exercise.date); put("name", exercise.name)
            put("minutes", exercise.minutes); put("intensity", exercise.intensity)
            put("kcal", exercise.kcal)
        })
        write(context, "exercises", arr)
    }

    /** Tất cả buổi vận động đã lưu, dùng tổng hợp tuần/tháng. */
    fun allExercises(context: Context): List<LinhExercise> {
        val saved = read(context, "exercises")
        return (0 until saved.length()).mapNotNull { index ->
            val record = saved.optJSONObject(index) ?: return@mapNotNull null
            LinhExercise(
                date = record.optString("date"),
                name = record.optString("name"),
                minutes = record.optInt("minutes"),
                intensity = record.optString("intensity"),
                kcal = record.optInt("kcal")
            )
        }
    }

    /** Nhật ký vận động cho một ngày được chọn. */
    fun exercisesFor(context: Context, date: String): List<LinhExercise> =
        allExercises(context).filter { it.date == date }
}
