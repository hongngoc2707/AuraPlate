package com.example.auraplate.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.MainActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivitySplashBinding
import com.example.auraplate.ui.auth.LoginActivity
import com.example.auraplate.utils.applySystemBars

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)
        // Figma 01: the CTA starts the journey. Do not auto-skip this screen.
        binding.btnStartJourney.setOnClickListener {
            val target = if (AppPreferences(this).isLoggedIn) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, target))
            finish()
        }
    }
}
