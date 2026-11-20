package com.example.auraplate.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Chừa khoảng trống cho thanh trạng thái và thanh điều hướng hệ thống.
 * Tránh cho nút quay lại bị che bởi đồng hồ hoặc camera trên Android mới.
 */
fun applySystemBars(root: View) {
    val originalLeft = root.paddingLeft
    val originalTop = root.paddingTop
    val originalRight = root.paddingRight
    val originalBottom = root.paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(
            originalLeft + bars.left,
            originalTop + bars.top,
            originalRight + bars.right,
            originalBottom + bars.bottom
        )
        insets
    }

    ViewCompat.requestApplyInsets(root)
}
