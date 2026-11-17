package com.example.auraplate.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.auraplate.MainActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.data.repository.MockDataRepository
import com.example.auraplate.databinding.FragmentHomeBinding
import com.example.auraplate.ui.food.AddFoodActivity
import com.example.auraplate.ui.goal.GoalEvaluationActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: AppPreferences
    private lateinit var repository: MockDataRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        preferences = AppPreferences(requireContext())
        repository = MockDataRepository.getInstance()

        loadHomeData()
        setupListeners()

        return binding.root
    }

    private fun loadHomeData() {
        if (_binding == null) return
        val user = preferences.getUser()
        var lastName = user.name
        if (lastName.contains(" ")) {
            val parts = lastName.split(" ")
            lastName = parts[parts.size - 1]
        }
        binding.tvHomeGreeting.text = "Chào buổi sáng, $lastName! 👋"

        val goalCal = 1700
        val consumed = repository.getTodayTotalCaloriesIn()
        val burned = repository.getTodayTotalCaloriesBurned()
        val remaining = goalCal - consumed + burned

        binding.tvCaloriesGoal.text = String.format("%,d", goalCal)
        binding.tvCaloriesConsumed.text = String.format("%,d", consumed)
        binding.tvCaloriesBurned.text = String.format("%,d", burned)
        binding.tvCaloriesRemaining.text = String.format("%,d", maxOf(0, remaining))

        val percent = minOf(100, Math.round(consumed.toFloat() / goalCal * 100))
        binding.progressCalorieCircle.progress = percent

        binding.tvWaterAmount.text = "${preferences.getWaterIntakeMl()} / 2,000 ml"
    }

    private fun setupListeners() {
        if (_binding == null) return

        binding.btnAddWaterHome.setOnClickListener {
            preferences.addWater(250)
            _binding?.tvWaterAmount?.text = "${preferences.getWaterIntakeMl()} / 2,000 ml"
            Toast.makeText(requireContext(), "💧 Đã ghi nhận thêm 250ml nước!", Toast.LENGTH_SHORT).show()
        }

        // Click Banner Goal -> Mở Màn hình 12 (Đánh giá tiến độ mục tiêu)
        binding.cardHomeGoalBanner.setOnClickListener {
            val intent = Intent(requireContext(), GoalEvaluationActivity::class.java)
            startActivity(intent)
        }

        // Click xem tất cả bữa ăn -> Chuyển sang Tab Ăn uống
        binding.tvViewAllMeals.setOnClickListener {
            (activity as? MainActivity)?.selectTab(1)
        }

        val openAddFood = View.OnClickListener {
            val intent = Intent(requireContext(), AddFoodActivity::class.java)
            startActivity(intent)
        }

        binding.cardMealBreakfast.setOnClickListener(openAddFood)
        binding.cardMealLunch.setOnClickListener(openAddFood)
        binding.cardMealDinner.setOnClickListener(openAddFood)
    }

    override fun onResume() {
        super.onResume()
        loadHomeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
