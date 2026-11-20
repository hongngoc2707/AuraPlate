package com.example.auraplate

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.auraplate.databinding.ActivityMainBinding
import com.example.auraplate.ui.dialog.QuickAddBottomSheet
import com.example.auraplate.ui.diary.FoodDiaryFragment
import com.example.auraplate.ui.exercise.ExerciseFragment
import com.example.auraplate.ui.home.HomeFragment
import com.example.auraplate.ui.profile.ProfileFragment
import com.example.auraplate.utils.applySystemBars

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_OPEN_TAB = "open_tab"
        private const val KEY_CURRENT_TAB = "current_tab"
    }

    private lateinit var binding: ActivityMainBinding
    private var currentTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)

        setupListeners()
        val requestedTab = savedInstanceState?.getInt(KEY_CURRENT_TAB)
            ?: intent.getIntExtra(EXTRA_OPEN_TAB, 0)

        if (savedInstanceState == null) {
            selectTab(requestedTab)
        } else {
            // FragmentManager tự khôi phục Fragment khi xoay màn hình.
            currentTabIndex = requestedTab.coerceIn(0, 3)
            updateTabStyles(currentTabIndex)
        }
        // Tab con: quay về Trang chủ; ở Trang chủ: để hệ thống đóng Activity.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentTabIndex != 0) {
                    selectTab(0)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
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

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        selectTab(intent.getIntExtra(EXTRA_OPEN_TAB, 0))
    }

    fun selectTab(index: Int) {
        val target = index.coerceIn(0, 3)
        if (currentTabIndex == target &&
            supportFragmentManager.findFragmentById(R.id.fragment_container) != null
        ) {
            updateTabStyles(target)
            return
        }

        currentTabIndex = target
        val fragment: Fragment = when (target) {
            1 -> FoodDiaryFragment()
            2 -> ExerciseFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateTabStyles(target)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_TAB, currentTabIndex)
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
