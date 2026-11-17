package com.example.auraplate.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val DAY_WEEK_FORMAT = SimpleDateFormat("EEEE, dd 'tháng' MM", Locale("vi", "VN"))

    @JvmStatic
    fun getTodayFormatted(): String = DATE_FORMAT.format(Date())

    @JvmStatic
    fun getTodayDayOfWeek(): String = DAY_WEEK_FORMAT.format(Date())

    @JvmStatic
    fun getCurrentTime(): String = TIME_FORMAT.format(Date())
}
