package com.example.auraplate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.auraplate.databinding.ActivityMainBinding
import com.example.auraplate.ui.dialog.QuickAddBottomSheet
import com.example.auraplate.ui.diary.FoodDiaryFragment
import com.example.auraplate.ui.exercise.ExerciseFragment
import com.example.auraplate.ui.home.HomeFragment
import com.example.auraplate.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        selectTab(0)
    }

    private fun setupListeners() {
        binding.navTabHome.setOnClickListener { selectTab(0) }
        binding.navTabDiet.setOnClickListener { selectTab(1) }
        binding.navTabExercise.setOnClickListener { selectTab(2) }
        binding.navTabProfile.setOnClickListener { selectTab(3) }

        binding.fabQuickAdd.setOnClickListener {
            val bottomSheet = QuickAddBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickAddBottomSheet")
        }
    }

    fun selectTab(index: Int) {
        currentTabIndex = index
        val fragment: Fragment = when (index) {
            1 -> FoodDiaryFragment()
            2 -> ExerciseFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateTabStyles(index)
    }

    private fun updateTabStyles(activeIndex: Int) {
        val activeColor = ContextCompat.getColor(this, R.color.primary_green)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_muted)

        binding.ivNavHome.setColorFilter(if (activeIndex == 0) activeColor else inactiveColor)
        binding.tvNavHome.setTextColor(if (activeIndex == 0) activeColor else inactiveColor)

        binding.ivNavDiet.setColorFilter(if (activeIndex == 1) activeColor else inactiveColor)
        binding.tvNavDiet.setTextColor(if (activeIndex == 1) activeColor else inactiveColor)

        binding.ivNavExercise.setColorFilter(if (activeIndex == 2) activeColor else inactiveColor)
        binding.tvNavExercise.setTextColor(if (activeIndex == 2) activeColor else inactiveColor)

        binding.ivNavProfile.setColorFilter(if (activeIndex == 3) activeColor else inactiveColor)
        binding.tvNavProfile.setTextColor(if (activeIndex == 3) activeColor else inactiveColor)
    }
}
