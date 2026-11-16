package com.example.auraplate.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.FragmentProfileBinding
import com.example.auraplate.ui.auth.LoginActivity
import com.example.auraplate.ui.goal.GoalEvaluationActivity
import com.example.auraplate.ui.goal.GoalSettingActivity
import com.example.auraplate.ui.goal.WeightProgressActivity
import com.example.auraplate.utils.BmiCalculator

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        preferences = AppPreferences(requireContext())

        loadUserData()
        setupListeners()

        return binding.root
    }

    private fun loadUserData() {
        if (_binding == null) return
        val user = preferences.getUser()
        binding.tvProfileName.text = user.name
        binding.tvProfileEmail.text = user.email
        binding.tvProfileHeight.text = "📏 ${user.heightCm} cm"
        binding.tvProfileWeight.text = "⚖️ ${user.currentWeightKg} kg"

        val bmi = BmiCalculator.calculateBmi(user.currentWeightKg, user.heightCm)
        binding.tvProfileBmi.text = "🎯 BMI ${String.format("%.1f", bmi)}"
    }

    private fun setupListeners() {
        if (_binding == null) return

        binding.btnMenuEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnMenuGoalSetting.setOnClickListener {
            val intent = Intent(requireContext(), GoalSettingActivity::class.java)
            startActivity(intent)
        }

        binding.btnMenuWeightProgress.setOnClickListener {
            val intent = Intent(requireContext(), WeightProgressActivity::class.java)
            startActivity(intent)
        }

        binding.btnMenuGoalEvaluation.setOnClickListener {
            val intent = Intent(requireContext(), GoalEvaluationActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            preferences.isLoggedIn = false
            Toast.makeText(requireContext(), "Đã đăng xuất khỏi AuraPlate", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
