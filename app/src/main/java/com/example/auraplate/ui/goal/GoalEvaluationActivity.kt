package com.example.auraplate.ui.goal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraplate.MainActivity
import com.example.auraplate.R
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityGoalEvaluationBinding
import com.example.auraplate.ui.dialog.QuickAddBottomSheet
import com.example.auraplate.ui.linh.LinhDataStore
import com.example.auraplate.utils.BmiCalculator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Màn hình 12: Đánh giá tiến độ AuraPlate.
 *
 * Các số liệu được đọc từ hồ sơ, mục tiêu và nhật ký người dùng đã lưu.
 * Không dùng điểm sức khỏe 88/100 hay xếp hạng tự tạo.
 */
class GoalEvaluationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalEvaluationBinding
    private lateinit var preferences: AppPreferences
    private var selectedPeriod: ReviewPeriod = ReviewPeriod.WEEK

    private enum class ReviewPeriod {
        WEEK,
        MONTH,
        ALL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoalEvaluationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppPreferences(this)

        selectedPeriod = when (savedInstanceState?.getString(KEY_PERIOD)) {
            ReviewPeriod.MONTH.name -> ReviewPeriod.MONTH
            ReviewPeriod.ALL.name -> ReviewPeriod.ALL
            else -> ReviewPeriod.WEEK
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        setupNavigation()
        setupPeriodButtons()
        render()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAdjustGoal.setOnClickListener {
            startActivity(Intent(this, GoalSettingActivity::class.java))
        }

        binding.btnExportReport.setOnClickListener {
            startActivity(Intent(this, WeightProgressActivity::class.java))
        }

        binding.navGoalHome.setOnClickListener { openMainTab(0) }
        binding.navGoalDiet.setOnClickListener { openMainTab(1) }
        binding.navGoalExercise.setOnClickListener { openMainTab(2) }
        binding.navGoalProfile.setOnClickListener { openMainTab(3) }
        binding.fabGoalQuickAdd.setOnClickListener {
            QuickAddBottomSheet().show(
                supportFragmentManager,
                "goal_review_quick_add"
            )
        }
    }

    private fun setupPeriodButtons() {
        binding.btnReviewWeek.setOnClickListener {
            selectPeriod(ReviewPeriod.WEEK)
        }
        binding.btnReviewMonth.setOnClickListener {
            selectPeriod(ReviewPeriod.MONTH)
        }
        binding.btnReviewAll.setOnClickListener {
            selectPeriod(ReviewPeriod.ALL)
        }
    }

    private fun selectPeriod(period: ReviewPeriod) {
        selectedPeriod = period
        render()
    }

    /** Hiển thị rõ bộ lọc đang chọn; không dùng nhiều màn hình cho các kỳ. */
    private fun updatePeriodStyles() {
        val buttons = mapOf(
            ReviewPeriod.WEEK to binding.btnReviewWeek,
            ReviewPeriod.MONTH to binding.btnReviewMonth,
            ReviewPeriod.ALL to binding.btnReviewAll
        )

        buttons.forEach { (period, button) ->
            val chosen = period == selectedPeriod
            button.setBackgroundResource(
                if (chosen) R.drawable.bg_fgm_button else R.drawable.bg_fgm_card
            )
            button.setTextColor(
                Color.parseColor(if (chosen) "#FFFFFF" else "#267D4C")
            )
            button.alpha = if (chosen) 1f else 0.92f
        }
    }

    /** Ngày bắt đầu chu kỳ, lưu dưới định dạng yyyy-MM-dd để so sánh an toàn. */
    private fun periodStart(): String? {
        if (selectedPeriod == ReviewPeriod.ALL) return null

        val calendar = Calendar.getInstance()
        if (selectedPeriod == ReviewPeriod.WEEK) {
            // Thứ hai là ngày đầu tuần.
            val offset = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            calendar.add(Calendar.DAY_OF_YEAR, -offset)
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        return isoDate(calendar)
    }

    private fun isoDate(calendar: Calendar): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

    private fun shortDate(iso: String): String {
        return try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso)
            SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(parsed!!)
        } catch (_: Exception) {
            iso
        }
    }

    private fun render() {
        updatePeriodStyles()
        showWeightAndBmi()
        showDiaryByPeriod()
    }

    /** Phân tích cân nặng, mục tiêu và BMI tham khảo theo hồ sơ. */
    private fun showWeightAndBmi() {
        val user = preferences.getUser()
        val hasProfile = preferences.hasSavedProfile()
        val hasGoal = preferences.hasSavedGoal() && user.targetWeightKg > 0f
        val startingWeight = preferences.initialWeightKg()
        val currentWeight = if (hasProfile) user.currentWeightKg else 0f
        val targetWeight = if (hasGoal) user.targetWeightKg else 0f

        binding.tvReviewWeight.text = if (hasProfile) formatKg(currentWeight) else "-- kg"
        binding.tvReviewGoalWeight.text = if (hasGoal) formatKg(targetWeight) else "-- kg"

        if (hasProfile && user.heightCm > 0f) {
            val bmi = BmiCalculator.calculateBmi(currentWeight, user.heightCm)
            binding.tvReviewBmi.text = "%.1f".format(Locale("vi", "VN"), bmi)
            binding.tvReviewBmiAdvice.text = when {
                user.age < 18 ->
                    "BMI cần được đối chiếu theo tuổi và giới tính ở người dưới 18 tuổi. " +
                        "Ứng dụng chỉ hiển thị giá trị tham khảo."
                bmi < 18.5f ->
                    "BMI dưới khoảng tham khảo 18,5–24,9 của người trưởng thành. " +
                        "Hãy chú ý ăn uống đủ chất và theo dõi xu hướng cân nặng."
                bmi < 25f ->
                    "BMI nằm trong khoảng tham khảo 18,5–24,9 của người trưởng thành. " +
                        "Tiếp tục duy trì thói quen ăn uống và vận động cân bằng."
                else ->
                    "BMI cao hơn khoảng tham khảo của người trưởng thành. " +
                        "Nên theo dõi thói quen dài hạn, không tự đặt mục tiêu giảm cân quá nhanh."
            }
        } else {
            binding.tvReviewBmi.text = "--"
            binding.tvReviewBmiAdvice.text =
                "Chưa đủ cân nặng hoặc chiều cao để tính BMI. Hãy cập nhật hồ sơ."
        }

        val progress: Int
        val progressLabel: String
        val progressDetail: String
        val weightAdvice: String

        if (!hasProfile || !hasGoal || startingWeight == null) {
            progress = 0
            progressLabel = "Chưa đủ dữ liệu"
            progressDetail = "Cần lưu hồ sơ và mục tiêu cân nặng để tính tiến độ."
            weightAdvice =
                "Hãy nhập số cân ban đầu, mục tiêu và cập nhật cân nặng theo thời gian."
        } else if (preferences.getGoalType() == "Duy trì cân nặng") {
            val difference = abs(currentWeight - targetWeight)
            progress = 0
            progressLabel = "Đang duy trì"
            progressDetail =
                "Hiện tại ${formatKg(currentWeight)} · Mục tiêu ${formatKg(targetWeight)}. " +
                    "Chênh lệch ${formatKg(difference)}."
            weightAdvice =
                "Mục tiêu duy trì không quy đổi thành % hoàn thành. " +
                    "Hãy quan sát mức chênh lệch qua các lần cân."
        } else {
            val totalChange = abs(targetWeight - startingWeight)
            val changedWeight = currentWeight - startingWeight
            val movingCorrectly =
                (targetWeight - startingWeight) * changedWeight >= 0f

            progress = if (totalChange < 0.1f) {
                100
            } else if (movingCorrectly) {
                ((abs(changedWeight) / totalChange) * 100f)
                    .roundToInt()
                    .coerceIn(0, 100)
            } else {
                0
            }

            progressLabel = "$progress% lộ trình"
            progressDetail =
                "Ban đầu ${formatKg(startingWeight)} · Hiện tại ${formatKg(currentWeight)} " +
                    "· Mục tiêu ${formatKg(targetWeight)}."

            weightAdvice = when {
                progress >= 100 ->
                    "Bạn đã chạm mức cân mục tiêu. Nên tiếp tục theo dõi và duy trì " +
                        "thói quen phù hợp, không thay đổi quá đột ngột."
                !movingCorrectly ->
                    "Cân nặng đang thay đổi ngược hướng mục tiêu. " +
                        "Hãy theo dõi thêm các lần đo trước khi điều chỉnh kế hoạch."
                progress == 0 ->
                    "Bạn đã thiết lập mục tiêu. Hãy cập nhật cân nặng định kỳ để " +
                        "thấy sự thay đổi trên biểu đồ."
                else ->
                    "Bạn đã đi được $progress% khoảng chênh lệch cân nặng cần đạt. " +
                        "Tiếp tục theo dõi đều đặn và ưu tiên thay đổi bền vững."
            }
        }

        binding.pbReviewProgress.progress = progress
        binding.tvReviewProgress.text = progressLabel
        binding.tvReviewProgressNote.text = progressDetail
        binding.tvReviewWeightAdvice.text = weightAdvice
    }

    /**
     * Tính số liệu từ các nhật ký thực sự đã lưu trên thiết bị.
     * Cùng nguồn dữ liệu với hai tab Ăn uống và Vận động.
     */
    private fun showDiaryByPeriod() {
        val fromDate = periodStart()
        val toDate = LinhDataStore.todayKey()

        val meals = LinhDataStore.allMeals(this).filter { item ->
            item.date <= toDate && (fromDate == null || item.date >= fromDate)
        }
        val exercises = LinhDataStore.allExercises(this).filter { item ->
            item.date <= toDate && (fromDate == null || item.date >= fromDate)
        }

        val periodName = when (selectedPeriod) {
            ReviewPeriod.WEEK -> "Tuần này"
            ReviewPeriod.MONTH -> "Tháng này"
            ReviewPeriod.ALL -> "Toàn lộ trình"
        }
        binding.tvReviewPeriodCaption.text = if (fromDate == null) {
            "Nhật ký: toàn bộ dữ liệu đến ${shortDate(toDate)}.\n" +
                "Cân nặng luôn phản ánh toàn lộ trình."
        } else {
            "Nhật ký $periodName: ${shortDate(fromDate)} – ${shortDate(toDate)}.\n" +
                "Cân nặng luôn phản ánh toàn lộ trình."
        }

        val foodCalories = meals.sumOf { it.kcal }
        val mealTypes = meals.map { "${it.date}:${it.mealType}" }.distinct().size
        val mealDays = meals.map { it.date }.distinct().size

        binding.tvReviewFood.text = if (meals.isEmpty()) {
            "Chưa ghi nhận"
        } else {
            "$foodCalories kcal đã nạp"
        }
        binding.tvReviewFoodNote.text = if (meals.isEmpty()) {
            "Chưa có món ăn nào trong kỳ đã chọn."
        } else {
            "${meals.size} mục thực phẩm · $mealTypes bữa · $mealDays ngày đã ghi."
        }
        binding.tvReviewFoodAdvice.text = when {
            meals.isEmpty() ->
                "Hãy thêm bữa ăn vào nhật ký. Ứng dụng chỉ tính năng lượng khi bạn " +
                    "ghi món và khẩu phần thực tế."
            mealTypes < 3 ->
                "Nhật ký mới có $mealTypes bữa. Hãy ghi thêm các bữa còn lại để " +
                    "tổng hợp đầy đủ hơn; ưu tiên thực phẩm đa dạng."
            else ->
                "Bạn đã ghi $mealTypes bữa trong kỳ. Tiếp tục phân bổ bữa hợp lý, " +
                    "bổ sung rau và uống nước theo nhu cầu."
        }

        val exerciseMinutes = exercises.sumOf { it.minutes }
        val exerciseCalories = exercises.sumOf { it.kcal }
        val exerciseDays = exercises.map { it.date }.distinct().size

        binding.tvReviewExercise.text = if (exercises.isEmpty()) {
            "Chưa ghi nhận"
        } else {
            "$exerciseMinutes phút · $exerciseCalories kcal"
        }
        binding.tvReviewExerciseNote.text = if (exercises.isEmpty()) {
            "Chưa có hoạt động nào trong kỳ đã chọn."
        } else {
            "${exercises.size} hoạt động trong $exerciseDays ngày · kcal tiêu hao ước tính."
        }
        binding.tvReviewExerciseAdvice.text = when {
            exercises.isEmpty() ->
                "Bắt đầu bằng hoạt động nhẹ phù hợp với thể trạng và ghi lại " +
                    "thời gian sau mỗi buổi tập."
            exerciseDays == 1 ->
                "Đã ghi vận động trong 1 ngày. Hãy cố gắng duy trì thói quen " +
                    "đều đặn và điều chỉnh cường độ theo sức khỏe."
            else ->
                "Bạn đã ghi hoạt động trong $exerciseDays ngày. " +
                    "Tiếp tục xen kẽ vận động và nghỉ ngơi; kcal chỉ là ước tính."
        }

        val hasProfile = preferences.hasSavedProfile()
        val hasGoal = preferences.hasSavedGoal()
        binding.tvReviewTip.text = when {
            !hasProfile ->
                "Bắt đầu từ việc thiết lập hồ sơ, sau đó thêm bữa ăn và vận động " +
                    "để có đánh giá dựa trên chính dữ liệu của bạn."
            !hasGoal ->
                "Hồ sơ đã có dữ liệu. Bạn nên thiết lập mục tiêu cân nặng " +
                    "để theo dõi tiến độ theo thời gian."
            meals.isEmpty() && exercises.isEmpty() ->
                "Mục tiêu đã được lưu. Hãy ghi ít nhất một bữa ăn và một hoạt động " +
                    "để phần đánh giá có cơ sở rõ ràng."
            else ->
                "Đã có dữ liệu trong ${periodName.lowercase(Locale("vi", "VN"))}! " +
                    "Duy trì ghi nhật ký và cập nhật cân nặng để nhìn rõ xu hướng."
        }
    }

    private fun formatKg(value: Float): String =
        "%.1f kg".format(Locale("vi", "VN"), value)

    private fun openMainTab(index: Int) {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_OPEN_TAB, index)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PERIOD, selectedPeriod.name)
    }

    companion object {
        private const val KEY_PERIOD = "selected_evaluation_period"
    }
}
