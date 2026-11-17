package com.example.auraplate.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraplate.data.local.AppPreferences
import com.example.auraplate.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = AppPreferences(this)
        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        val user = preferences.getUser()
        binding.edtEditName.setText(user.name)
        binding.edtEditEmail.setText(user.email)
        binding.edtEditHeight.setText(user.heightCm.toString())
        binding.edtEditWeight.setText(user.currentWeightKg.toString())
    }

    private fun setupListeners() {
        binding.btnBackEditProfile.setOnClickListener { finish() }

        binding.btnSaveProfile.setOnClickListener {
            try {
                val name = binding.edtEditName.text.toString().trim()
                val email = binding.edtEditEmail.text.toString().trim()
                val height = binding.edtEditHeight.text.toString().trim().toFloat()
                val weight = binding.edtEditWeight.text.toString().trim().toFloat()

                val user = preferences.getUser()
                user.name = name
                user.email = email
                user.heightCm = height
                user.currentWeightKg = weight
                preferences.saveUser(user)

                Toast.makeText(this, "Đã cập nhật thông tin hồ sơ!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
