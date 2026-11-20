package com.example.auraplate.data.local

import android.content.Context
import com.example.auraplate.data.model.WeightRecord
import org.json.JSONArray
import org.json.JSONObject

/** Lịch sử cân nặng thực sự đã nhập, lưu cục bộ cho bản demo môn học. */
object WeightHistoryStore {
    private const val NAME = "auraplate_weight_history_v2"
    private const val KEY = "records"

    fun getRecords(context: Context): List<WeightRecord> {
        val raw = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val array = try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
        return (0 until array.length()).mapNotNull { index ->
            val obj = array.optJSONObject(index) ?: return@mapNotNull null
            val weight = obj.optDouble("weight", 0.0).toFloat()
            if (weight <= 0f) return@mapNotNull null
            WeightRecord(
                date = obj.optString("date", ""),
                weightKg = weight,
                bmi = obj.optDouble("bmi", 0.0).toFloat(),
                note = obj.optString("note", "")
            )
        }
    }

    fun addRecord(context: Context, record: WeightRecord) {
        val current = getRecords(context).toMutableList()
        current.add(record)
        write(context, current)
    }

    /** Không thêm lại dữ liệu khởi đầu khi cùng ngày đã có bản ghi. */
    fun addRecordIfDateAbsent(context: Context, record: WeightRecord) {
        if (getRecords(context).none { it.date == record.date }) addRecord(context, record)
    }

    private fun write(context: Context, records: List<WeightRecord>) {
        val arr = JSONArray()
        records.forEach { r ->
            arr.put(JSONObject().apply {
                put("date", r.date)
                put("weight", r.weightKg.toDouble())
                put("bmi", r.bmi.toDouble())
                put("note", r.note)
            })
        }
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }
}
