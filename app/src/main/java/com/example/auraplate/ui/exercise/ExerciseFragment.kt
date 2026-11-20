package com.example.auraplate.ui.exercise

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.auraplate.MainActivity
import com.example.auraplate.R
import com.example.auraplate.ui.linh.LinhDataStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Màn hình 09: Nhật ký vận động.
 * Lịch chọn ngày mở ngay dưới thanh ngày, đồng bộ với Nhật ký ăn uống.
 */
class ExerciseFragment : Fragment() {

    private val selectedDay: Calendar = Calendar.getInstance()
    private var page: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page = view

        savedInstanceState?.getLong(KEY_SELECTED_DATE)?.let { savedTime ->
            selectedDay.timeInMillis = savedTime
        }

        view.findViewById<View>(R.id.exerciseBack).setOnClickListener {
            (activity as? MainActivity)?.selectTab(0)
        }

        setupCalendar(view)
        view.findViewById<View>(R.id.exerciseAdd).setOnClickListener {
            val intent = Intent(requireContext(), AddExerciseActivity::class.java).apply {
                putExtra(AddExerciseActivity.EXTRA_DATE, dateKey())
            }
            startActivity(intent)
        }
        refresh()
    }

    private fun setupCalendar(root: View) {
        val calendarCard = root.findViewById<View>(R.id.exerciseCalendarCard)
        val calendar = root.findViewById<CalendarView>(R.id.exerciseCalendar)
        val dateLabel = root.findViewById<TextView>(R.id.exerciseDateText)

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

        root.findViewById<View>(R.id.exerciseDateToday).setOnClickListener {
            selectedDay.timeInMillis = System.currentTimeMillis()
            calendarCard.visibility = View.GONE
            refresh()
        }

        root.findViewById<View>(R.id.exercisePrevDate).setOnClickListener {
            selectedDay.add(Calendar.DAY_OF_MONTH, -1)
            calendarCard.visibility = View.GONE
            refresh()
        }

        root.findViewById<View>(R.id.exerciseNextDate).setOnClickListener {
            selectedDay.add(Calendar.DAY_OF_MONTH, 1)
            calendarCard.visibility = View.GONE
            refresh()
        }
    }

    private fun dateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDay.time)
    }

    private fun refresh() {
        val root = page ?: return
        val dayKey = dateKey()
        val exercises = LinhDataStore.exercisesFor(requireContext(), dayKey)
        val minutes = exercises.sumOf { it.minutes }
        val kcal = exercises.sumOf { it.kcal }

        val dateLabel = if (dayKey == LinhDataStore.todayKey()) {
            "Hôm nay, ${LinhDataStore.showDate(dayKey)} ▾"
        } else {
            "${LinhDataStore.showDate(dayKey)} ▾"
        }
        root.findViewById<TextView>(R.id.exerciseDateText).text = dateLabel
        root.findViewById<TextView>(R.id.exerciseTotalMinutes).text = "$minutes phút"
        root.findViewById<TextView>(R.id.exerciseTotalKcal).text =
            "🔥  $kcal kcal tiêu hao"
        root.findViewById<ProgressBar>(R.id.exerciseProgress).progress =
            minutes.coerceIn(0, 30)

        val tip = when {
            exercises.isEmpty() ->
                "Chưa có hoạt động trong ngày. Hãy chọn một hoạt động nhẹ phù hợp " +
                    "và ghi thời gian sau khi tập."
            minutes < 30 ->
                "Bạn đã vận động $minutes phút. Nếu thể trạng cho phép, hãy " +
                    "duy trì thêm vận động nhẹ và nghỉ ngơi hợp lý."
            else ->
                "Đã ghi $minutes phút vận động trong ngày. Hãy tiếp tục tập đều, " +
                    "lựa chọn cường độ phù hợp và nghỉ ngơi đầy đủ."
        }
        root.findViewById<TextView>(R.id.exerciseTipText).text = tip

        val list = root.findViewById<LinearLayout>(R.id.exerciseListContainer)
        list.removeAllViews()

        if (exercises.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "Chưa ghi nhận vận động trong ngày.\nBấm '+ Thêm hoạt động' để bắt đầu."
                textSize = 14f
                setTextColor(Color.parseColor("#6B8272"))
                setPadding(dp(18), dp(22), dp(18), dp(22))
                setBackgroundResource(R.drawable.bg_fgm_card)
            }
            list.addView(emptyText)
        } else {
            exercises.asReversed().forEach { item ->
                val itemView = TextView(requireContext()).apply {
                    text = "${item.name}\n${item.minutes} phút · ${item.kcal} kcal · ${item.intensity}"
                    textSize = 14f
                    setTextColor(Color.parseColor("#1F3D2E"))
                    setLineSpacing(dp(3).toFloat(), 1f)
                    setPadding(dp(18), dp(15), dp(18), dp(15))
                    setBackgroundResource(R.drawable.bg_fgm_card)
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10) }
                list.addView(itemView, params)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

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
        private const val KEY_SELECTED_DATE = "exercise_selected_date"
    }
}
