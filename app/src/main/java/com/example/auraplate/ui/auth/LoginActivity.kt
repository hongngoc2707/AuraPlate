package com.example.auraplate.ui.auth

import android.content.Intent
import android.util.Patterns
import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.MainActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityLoginBinding
import com.example.auraplate.ui.splash.SplashActivity
import com.example.auraplate.ui.profile.SetupProfileActivity
import com.example.auraplate.utils.applySystemBars

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)

        preferences = AppPreferences(this)
        setupListeners()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = returnToSplash()
            }
        )
    }

    private fun setupListeners() {
        binding.btnBackLogin.setOnClickListener { returnToSplash() }
        binding.btnLogin.setOnClickListener { performLogin() }

        binding.btnSkipDemo.setOnClickListener {
            // Dùng thử giao diện không yêu cầu hồ sơ, chỉ hiển thị dữ liệu trống.
            preferences.isLoggedIn = true
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Chức năng gửi email khôi phục sẽ được tích hợp sau", Toast.LENGTH_SHORT).show()
        }
    }

    private fun returnToSplash() {
        val intent = Intent(this, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun performLogin() {
        val email = binding.edtEmail.text?.toString()?.trim().orEmpty()
        val password = binding.edtPassword.text?.toString()?.trim().orEmpty()

        if (email.isEmpty()) {
            binding.edtEmail.error = "Vui lòng nhập email"
            binding.edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Địa chỉ email không hợp lệ"
            binding.edtEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.edtPassword.error = "Vui lòng nhập mật khẩu"
            binding.edtPassword.requestFocus()
            return
        }

        if (password.length < 8) {
            binding.edtPassword.error = "Mật khẩu phải từ 8 ký tự trở lên"
            binding.edtPassword.requestFocus()
            return
        }

        val user = preferences.getUser()
        user.email = email
        preferences.saveUser(user)

        // Bản báo cáo lần 1 dùng đăng nhập demo cục bộ, chưa có máy chủ xác thực.
        if (preferences.hasSavedProfile()) {
            preferences.isLoggedIn = true
            Toast.makeText(this, "Chào mừng ${user.name}!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        } else {
            // Chưa có hồ sơ: hướng người dùng nhập tuổi, chiều cao, cân nặng.
            val intent = Intent(this, SetupProfileActivity::class.java).apply {
                putExtra(SetupProfileActivity.EXTRA_FROM_REGISTER, false)
            }
            startActivity(intent)
        }
    }
}
