package com.example.auraplate.ui.auth

import android.content.Intent
import android.util.Patterns
import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityRegisterBinding
import com.example.auraplate.ui.profile.SetupProfileActivity
import com.example.auraplate.utils.applySystemBars

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBars(binding.root)

        preferences = AppPreferences(this)
        setupListeners()
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = returnToLogin()
            }
        )
    }

    private fun setupListeners() {
        binding.btnBackRegister.setOnClickListener { returnToLogin() }
        binding.tvGoToLogin.setOnClickListener { returnToLogin() }
        binding.btnSubmitRegister.setOnClickListener { performRegistration() }
    }

    /** Trở lại đúng màn hình đăng nhập, kể cả khi mở màn hình Đăng ký trực tiếp. */
    private fun returnToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        finish()
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

        if (pass.length < 8) {
            binding.edtRegPass.error = "Mật khẩu phải từ 8 ký tự trở lên"
            binding.edtRegPass.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtRegEmail.error = "Địa chỉ email không hợp lệ"
            binding.edtRegEmail.requestFocus()
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
        // Không đóng RegisterActivity tại đây: cho phép quay lại sửa thông tin.
        val intent = Intent(this, SetupProfileActivity::class.java).apply {
            putExtra(SetupProfileActivity.EXTRA_FROM_REGISTER, true)
        }
        startActivity(intent)
    }
}
