package com.example.auraplate.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.MainActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener { performLogin() }

        binding.btnSkipDemo.setOnClickListener {
            preferences.isLoggedIn = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Hệ thống đã gửi liên kết khôi phục tới email của bạn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogin() {
        val email = binding.edtEmail.text?.toString()?.trim().orEmpty()
        val password = binding.edtPassword.text?.toString()?.trim().orEmpty()

        if (email.isEmpty()) {
            binding.edtEmail.error = "Vui lòng nhập email"
            binding.edtEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.edtPassword.error = "Vui lòng nhập mật khẩu"
            binding.edtPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.edtPassword.error = "Mật khẩu phải từ 6 ký tự trở lên"
            binding.edtPassword.requestFocus()
            return
        }

        val user = preferences.getUser()
        user.email = email
        preferences.saveUser(user)
        preferences.isLoggedIn = true

        Toast.makeText(this, "Đăng nhập thành công! Chào mừng ${user.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
