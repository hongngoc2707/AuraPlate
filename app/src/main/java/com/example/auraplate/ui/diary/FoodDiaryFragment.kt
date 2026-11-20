package com.example.auraplate.ui.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.auraplate.MainActivity
import com.example.auraplate.R
import com.example.auraplate.ui.food.AddFoodActivity
import com.example.auraplate.ui.linh.LinhDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Màn hình 07: Nhật ký ăn uống.
 * Có thể chạm vào dòng ngày để mở lịch ngay bên dưới và chọn ngày/tháng.
 */
class FoodDiaryFragment : Fragment() {

    private val selectedDay: Calendar = Calendar.getInstance()
    private var page: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_food_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page = view

        savedInstanceState?.getLong(KEY_SELECTED_DATE)?.let { savedTime ->
            selectedDay.timeInMillis = savedTime
        }

        view.findViewById<View>(R.id.diaryBack).setOnClickListener {
            (activity as? MainActivity)?.selectTab(0)
        }

        setupCalendar(view)
        setupMealButtons(view)
        refresh()
    }

    /** Lịch hiển thị ngay bên dưới thanh chọn ngày; chạm ngày sẽ tự thu gọn. */
    private fun setupCalendar(root: View) {
        val calendarCard = root.findViewById<View>(R.id.diaryCalendarCard)
        val calendar = root.findViewById<CalendarView>(R.id.diaryCalendar)
        val dateLabel = root.findViewById<TextView>(R.id.diaryDateText)

        dateLabel.setOnClickListener {
            if (calendarCard.visibility == View.VISIBLE) {
                calendarCard.visibility = View.GONE
            } else {
                calendar.date = selectedDay.timeInMillis
                calendarCard.visibility = View.VISIBLE
            }
        }

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDay.set(year, month, dayOfMonth)
            calendarCard.visibility = View.GONE
            refresh()
        }

        root.findViewById<View>(R.id.diaryDateToday).setOnClickListener {
            selectedDay.timeInMillis = System.currentTimeMillis()
            calendarCard.visibility = View.GONE
            refresh()
        }

        root.findViewById<View>(R.id.diaryPrevDate).setOnClickListener {
            selectedDay.add(Calendar.DAY_OF_MONTH, -1)
            calendarCard.visibility = View.GONE
            refresh()
        }

        root.findViewById<View>(R.id.diaryNextDate).setOnClickListener {
            selectedDay.add(Calendar.DAY_OF_MONTH, 1)
            calendarCard.visibility = View.GONE
            refresh()
        }
    }

    private fun setupMealButtons(root: View) {
        val mealButtons = mapOf(
            R.id.diaryBreakfastAdd to LinhDataStore.BREAKFAST,
            R.id.diaryLunchAdd to LinhDataStore.LUNCH,
            R.id.diaryDinnerAdd to LinhDataStore.DINNER,
            R.id.diarySnackAdd to LinhDataStore.SNACK
        )

        mealButtons.forEach { (buttonId, mealType) ->
            root.findViewById<View>(buttonId).setOnClickListener {
                val intent = Intent(requireContext(), AddFoodActivity::class.java).apply {
                    putExtra(AddFoodActivity.EXTRA_MEAL_TYPE, mealType)
                    putExtra(AddFoodActivity.EXTRA_DATE, dateKey())
                }
                startActivity(intent)
            }
        }
    }

    private fun dateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDay.time)
    }

    private fun refresh() {
        val root = page ?: return
        val dayKey = dateKey()
        val dateLabel = if (dayKey == LinhDataStore.todayKey()) {
            "Hôm nay, ${LinhDataStore.showDate(dayKey)} ▾"
        } else {
            "${LinhDataStore.showDate(dayKey)} ▾"
        }
        root.findViewById<TextView>(R.id.diaryDateText).text = dateLabel

        val meals = LinhDataStore.mealsFor(requireContext(), dayKey)
        val totalCalories = meals.sumOf { it.kcal }

        root.findViewById<TextView>(R.id.diaryTotalKcal).text = "$totalCalories kcal"
        root.findViewById<ProgressBar>(R.id.diaryProgress).progress =
            totalCalories.coerceIn(0, 2000)

        val mealRows = listOf(
            LinhDataStore.BREAKFAST to R.id.diaryBreakfastInfo,
            LinhDataStore.LUNCH to R.id.diaryLunchInfo,
            LinhDataStore.DINNER to R.id.diaryDinnerInfo,
            LinhDataStore.SNACK to R.id.diarySnackInfo
        )

        mealRows.forEach { (type, textId) ->
            val items = meals.filter { it.mealType == type }
            val info = if (items.isEmpty()) {
                "Chưa ghi nhận món"
            } else {
                val quantity = items.sumOf { it.quantity }
                val kcal = items.sumOf { it.kcal }
                val foodNames = items.joinToString(", ") { it.name }
                "$quantity khẩu phần · $kcal kcal\n$foodNames"
            }
            root.findViewById<TextView>(textId).text = info
        }

        val recordedMealTypes = meals.map { it.mealType }.distinct().size
        val tip = when {
            meals.isEmpty() ->
                "Chưa có món ăn trong ngày này. Chọn dấu (+) ở bữa phù hợp để bắt đầu."
            recordedMealTypes < 3 ->
                "Đã ghi $recordedMealTypes bữa. Bạn có thể bổ sung các bữa còn lại " +
                    "để nhật ký đầy đủ hơn."
            else ->
                "Bạn đã ghi $recordedMealTypes bữa. Hãy duy trì thực phẩm đa dạng " +
                    "và uống nước theo nhu cầu."
        }
        root.findViewById<TextView>(R.id.diaryTipText).text = tip
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_SELECTED_DATE, selectedDay.timeInMillis)
    }

    override fun onResume() {
        super.onResume()
        if (page != null) refresh()
    }

    override fun onDestroyView() {
        page = null
        super.onDestroyView()
    }

    companion object {
        private const val KEY_SELECTED_DATE = "diary_selected_date"
    }
}
