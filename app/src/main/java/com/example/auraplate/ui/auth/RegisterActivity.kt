package com.example.auraplate.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityRegisterBinding
import com.example.auraplate.ui.profile.SetupProfileActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBackRegister.setOnClickListener { finish() }
        binding.tvGoToLogin.setOnClickListener { finish() }
        binding.btnSubmitRegister.setOnClickListener { performRegistration() }
    }

    private fun performRegistration() {
        val name = binding.edtFullName.text?.toString()?.trim().orEmpty()
        val email = binding.edtRegEmail.text?.toString()?.trim().orEmpty()
        val pass = binding.edtRegPass.text?.toString()?.trim().orEmpty()
        val confirmPass = binding.edtConfirmPass.text?.toString()?.trim().orEmpty()

        if (name.isEmpty()) {
            binding.edtFullName.error = "Vui lòng nhập họ và tên"
            binding.edtFullName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.edtRegEmail.error = "Vui lòng nhập email"
            binding.edtRegEmail.requestFocus()
            return
        }

        if (pass.length < 6) {
            binding.edtRegPass.error = "Mật khẩu phải từ 6 ký tự trở lên"
            binding.edtRegPass.requestFocus()
            return
        }

        if (pass != confirmPass) {
            binding.edtConfirmPass.error = "Mật khẩu xác nhận không trùng khớp"
            binding.edtConfirmPass.requestFocus()
            return
        }

        if (!binding.cbAgreeTerms.isChecked) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản sử dụng", Toast.LENGTH_SHORT).show()
            return
        }

        val user = preferences.getUser()
        user.name = name
        user.email = email
        preferences.saveUser(user)

        Toast.makeText(this, "Tạo tài khoản thành công! Hãy thiết lập hồ sơ", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SetupProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}
