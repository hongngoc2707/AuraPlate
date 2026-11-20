package com.example.auraplate.ui.goal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.auraplate.data.model.WeightRecord
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/** Biểu đồ tự vẽ từ các lần cân thực, không phải ảnh số liệu mẫu. */
class WeightTrendView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private var records = emptyList<WeightRecord>()
    private var targetKg: Float? = null
    private val density = resources.displayMetrics.density
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#738779")
        textSize = 11f * resources.displayMetrics.scaledDensity
    }
    private val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DEE9E0"); strokeWidth = 1f * density
    }
    private val actual = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#267D4C"); strokeWidth = 2.6f * density
        style = Paint.Style.STROKE; strokeJoin = Paint.Join.ROUND
    }
    private val dot = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#267D4C") }
    private val goal = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B2CFBD"); strokeWidth = 1.7f * density
        pathEffect = DashPathEffect(floatArrayOf(7f * density, 6f * density), 0f)
    }

    fun show(records: List<WeightRecord>, targetKg: Float?) {
        this.records = records.takeLast(6)
        this.targetKg = targetKg?.takeIf { it > 0f }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (records.isEmpty()) {
            val empty = "Chưa có dữ liệu cân nặng"
            canvas.drawText(empty, (w - text.measureText(empty)) / 2f, h / 2f, text)
            return
        }
        val left = 48f * density
        val right = w - 15f * density
        val top = 18f * density
        val bottom = h - 37f * density
        val all = records.map { it.weightKg } + listOfNotNull(targetKg)
        val lowest = all.minOrNull() ?: 0f
        val highest = all.maxOrNull() ?: 100f
        val minY = lowest - max(1f, (highest - lowest) * 0.20f)
        val maxY = highest + max(1f, (highest - lowest) * 0.20f)
        val range = max(0.1f, maxY - minY)
        val yFor: (Float) -> Float = { bottom - (it - minY) / range * (bottom - top) }
        val xFor: (Int) -> Float = { i ->
            if (records.size == 1) (left + right) / 2f
            else left + i.toFloat() / (records.size - 1) * (right - left)
        }
        for (step in 0..3) {
            val value = minY + step * range / 3f
            val y = yFor(value)
            canvas.drawLine(left, y, right, y, grid)
            canvas.drawText(String.format(Locale.US, "%.1f", value), 3f * density, y + 4f * density, text)
        }
        targetKg?.let { value ->
            canvas.drawLine(left, yFor(value), right, yFor(value), goal)
        }
        val path = Path()
        records.forEachIndexed { index, record ->
            val x = xFor(index)
            val y = yFor(record.weightKg)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, actual)
        records.forEachIndexed { index, record ->
            val x = xFor(index)
            canvas.drawCircle(x, yFor(record.weightKg), 4.5f * density, dot)
        }
        // Để chữ không chồng nhau, chỉ hiển thị ngày đầu và ngày cuối.
        val start = records.first().date
        val end = records.last().date
        canvas.drawText(start, left, h - 12f * density, text)
        if (records.size > 1) {
            canvas.drawText(end, right - text.measureText(end), h - 12f * density, text)
        }
    }
}
