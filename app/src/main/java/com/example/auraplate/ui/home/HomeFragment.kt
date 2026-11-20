package com.example.auraplate.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.auraplate.MainActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.FragmentHomeBinding
import com.example.auraplate.ui.food.AddFoodActivity
import com.example.auraplate.ui.linh.LinhDataStore
import com.example.auraplate.ui.goal.GoalSettingActivity
import com.example.auraplate.ui.goal.WeightProgressActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.ivHomeAvatar.setOnClickListener { (activity as? MainActivity)?.selectTab(3) }
        binding.quickDiet.setOnClickListener { (activity as? MainActivity)?.selectTab(1) }
        binding.quickExercise.setOnClickListener { (activity as? MainActivity)?.selectTab(2) }
        binding.quickGoal.setOnClickListener { startActivity(Intent(requireContext(), GoalSettingActivity::class.java)) }
        binding.quickProgress.setOnClickListener { startActivity(Intent(requireContext(), WeightProgressActivity::class.java)) }
        binding.btnHomeAddFood.setOnClickListener { startActivity(Intent(requireContext(), AddFoodActivity::class.java)) }
        // Two recent-log cards are summaries only; bottom navigation opens the full history.
        render()
        return binding.root
    }
    private fun render() {
        if (_binding == null) return
        val prefs = AppPreferences(requireContext())
        val user = prefs.getUser()
        binding.tvHomeGreeting.text = "Xin chào, ${user.name.substringAfterLast(' ')}!"
        binding.tvLatestWeight.text = if (prefs.hasSavedProfile()) "${user.currentWeightKg} kg" else "-- kg"
        val loggedMeals = LinhDataStore.mealsFor(requireContext(), LinhDataStore.todayKey())
        val loggedExercises = LinhDataStore.exercisesFor(requireContext(), LinhDataStore.todayKey())
        val calories = loggedMeals.sumOf { it.kcal }
        val burned = loggedExercises.sumOf { it.kcal }
        binding.tvCaloriesConsumed.text = "$calories"
        binding.tvCaloriesBurned.text = "$burned kcal"
        binding.progressCalorieCircle.progress = ((calories.toFloat() / 1700) * 100).toInt().coerceIn(0,100)
        val recentMeal = loggedMeals.lastOrNull()
        binding.tvLatestFood.text = recentMeal?.let { "Bữa ${it.mealType.lowercase()} · ${it.name}" }
            ?: "Chưa có bữa ăn nào"
        binding.tvLatestFoodKcal.text = recentMeal?.let { "${it.kcal} kcal · ${it.quantity} khẩu phần" }
            ?: "-- kcal"
        val recentActivity = loggedExercises.lastOrNull()
        binding.tvLatestExercise.text = recentActivity?.let { "${it.name} · ${it.minutes} phút" }
            ?: "Chưa có hoạt động"
        binding.tvLatestExerciseKcal.text = recentActivity?.let { "${it.kcal} kcal tiêu hao" }
            ?: "-- kcal"
    }
    override fun onResume() { super.onResume(); render() }
    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
