package com.example.auraplate.ui.food

import android.graphics.Color
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.R
import com.example.auraplate.ui.dialog.FoodSearchDialog
import com.example.auraplate.ui.linh.LinhDataStore
import com.example.auraplate.ui.linh.LinhFood
import com.example.auraplate.utils.applySystemBars

/** 08_ThemMonAn: chọn bữa, tìm món, thêm / giảm / xóa khẩu phần và lưu. */
class AddFoodActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MEAL_TYPE = "meal_type"
        const val EXTRA_DATE = "meal_date"
    }
    private val quantities = linkedMapOf<String, Int>()
    private var mealType = LinhDataStore.BREAKFAST
    private var date = LinhDataStore.todayKey()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        applySystemBars(findViewById(android.R.id.content))

        mealType = intent.getStringExtra(EXTRA_MEAL_TYPE) ?: LinhDataStore.BREAKFAST
        date = intent.getStringExtra(EXTRA_DATE) ?: LinhDataStore.todayKey()

        findViewById<View>(R.id.foodBack).setOnClickListener { finish() }
        val buttons = listOf(
            Triple(R.id.foodMealBreakfast, LinhDataStore.BREAKFAST, 0),
            Triple(R.id.foodMealLunch, LinhDataStore.LUNCH, 0),
            Triple(R.id.foodMealDinner, LinhDataStore.DINNER, 0),
            Triple(R.id.foodMealSnack, LinhDataStore.SNACK, 0)
        )
        buttons.forEach { (id, type, _) ->
            findViewById<View>(id).setOnClickListener { mealType = type; refreshMealTabs() }
        }
        findViewById<View>(R.id.foodOpenSearch).setOnClickListener {
            FoodSearchDialog().show(supportFragmentManager, "food_search")
        }
        supportFragmentManager.setFragmentResultListener(LinhDataStore.RESULT_FOOD, this) { _, bundle ->
            bundle.getString("food_id")?.let { addFood(it) }
        }
        findViewById<View>(R.id.foodQuickRice).setOnClickListener { addFood("rice") }
        findViewById<View>(R.id.foodQuickEgg).setOnClickListener { addFood("egg") }
        findViewById<View>(R.id.foodQuickBroccoli).setOnClickListener { addFood("broccoli") }
        findViewById<View>(R.id.foodSave).setOnClickListener {
            if (quantities.isEmpty()) {
                Toast.makeText(this, "Hãy chọn ít nhất một món ăn", Toast.LENGTH_SHORT).show()
            } else {
                LinhDataStore.saveMeal(this, date, mealType, quantities)
                Toast.makeText(this, "Đã lưu bữa $mealType", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        refreshMealTabs()
        renderSelectedFoods()
    }

    private fun refreshMealTabs() {
        val styles = listOf(
            Triple(R.id.foodMealBreakfast, LinhDataStore.BREAKFAST, R.drawable.bg_meal_breakfast to R.drawable.bg_meal_breakfast_selected),
            Triple(R.id.foodMealLunch, LinhDataStore.LUNCH, R.drawable.bg_meal_lunch to R.drawable.bg_meal_lunch_selected),
            Triple(R.id.foodMealDinner, LinhDataStore.DINNER, R.drawable.bg_meal_dinner to R.drawable.bg_meal_dinner_selected),
            Triple(R.id.foodMealSnack, LinhDataStore.SNACK, R.drawable.bg_meal_snack to R.drawable.bg_meal_snack_selected)
        )
        styles.forEach { (viewId, name, backgrounds) ->
            findViewById<View>(viewId).setBackgroundResource(
                if (name == mealType) backgrounds.second else backgrounds.first
            )
        }
    }

    private fun addFood(id: String) {
        if (LinhDataStore.foodById(id) == null) return
        quantities[id] = (quantities[id] ?: 0) + 1
        renderSelectedFoods()
    }
    private fun changeQuantity(id: String, delta: Int) {
        val next = (quantities[id] ?: 0) + delta
        if (next <= 0) quantities.remove(id) else quantities[id] = next
        renderSelectedFoods()
    }

    private fun renderSelectedFoods() {
        val container = findViewById<LinearLayout>(R.id.foodSelectedContainer)
        container.removeAllViews()
        if (quantities.isEmpty()) {
            container.addView(makeLabel("Chưa chọn món nào. Nhấn (+) hoặc tìm món ăn.", 13, false,
                "#819588").apply { gravity = Gravity.CENTER; setPadding(4.dp(), 25.dp(), 4.dp(), 25.dp()) })
        }
        quantities.forEach { (id, qty) ->
            val food = LinhDataStore.foodById(id) ?: return@forEach
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(6.dp(), 9.dp(), 6.dp(), 9.dp())
            }
            row.addView(ImageView(this).apply {
                setImageResource(LinhDataStore.foodImage(food.id))
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = food.name
            }, LinearLayout.LayoutParams(45.dp(), 45.dp()))
            val name = makeLabel(" ${food.name}\n ${food.kcal * qty} kcal / $qty phần", 13, true)
            row.addView(name, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(actionText("−") { changeQuantity(id, -1) })
            row.addView(makeLabel("$qty", 13, true).apply { gravity = Gravity.CENTER },
                LinearLayout.LayoutParams(26.dp(), 40.dp()))
            row.addView(actionText("+") { changeQuantity(id, 1) })
            row.addView(actionText("✕") { quantities.remove(id); renderSelectedFoods() })
            container.addView(row)
            container.addView(View(this).apply { setBackgroundColor(Color.parseColor("#EDF3EE")) },
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.dp()))
        }
        val kcal = quantities.entries.sumOf { (id, qty) ->
            (LinhDataStore.foodById(id)?.kcal ?: 0) * qty
        }
        findViewById<TextView>(R.id.foodTotalKcal).text = "$kcal kcal"
    }

    private fun makeLabel(value: String, size: Int, bold: Boolean, color: String = "#1F3D2E") =
        TextView(this).apply {
            text = value; textSize = size.toFloat(); setTextColor(Color.parseColor(color))
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
    private fun actionText(label: String, listener: () -> Unit) =
        makeLabel(label, 20, true, "#267D4C").apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(34.dp(), 40.dp())
            setOnClickListener { listener() }
            contentDescription = label
        }
    private fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
