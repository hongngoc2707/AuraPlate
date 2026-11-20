package com.example.auraplate.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.auraplate.R
import com.example.auraplate.ui.linh.LinhDataStore
import com.example.auraplate.ui.linh.LinhFood

/** Overlay_TimMonAn: lọc tên món; bấm (+) trả món đã chọn cho AddFoodActivity. */
class FoodSearchDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_food_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.foodDialogClose).setOnClickListener { dismiss() }
        val input = view.findViewById<EditText>(R.id.foodDialogQuery)
        showFoods(view, "")
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showFoods(view, s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.92f).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setDimAmount(0.35f)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun showFoods(root: View, query: String) {
        val list = root.findViewById<LinearLayout>(R.id.foodDialogResults)
        list.removeAllViews()
        val normalized = query.trim().lowercase(java.util.Locale.ROOT)
        val foods = LinhDataStore.foods.filter { it.name.lowercase(java.util.Locale.ROOT).contains(normalized) }
        if (foods.isEmpty()) {
            list.addView(TextView(requireContext()).apply {
                text = "Không tìm thấy món ăn phù hợp"
                textSize = 14f; setPadding(12, 18, 12, 18)
            })
        }
        foods.forEach { food -> list.addView(createRow(food)) }
    }

    private fun createRow(food: LinhFood): View {
        val ctx = requireContext()
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.bg_fgm_card)
            val px = (8 * resources.displayMetrics.density).toInt()
            setPadding(px, px, px, px)
        }
        val dp = resources.displayMetrics.density
        row.addView(ImageView(ctx).apply {
            setImageResource(LinhDataStore.foodImage(food.id))
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = food.name
        }, LinearLayout.LayoutParams((49 * dp).toInt(), (49 * dp).toInt()))
        val name = TextView(ctx).apply {
            text = "  ${food.name}\n  ${food.kcal} kcal / ${food.unit}"
            textSize = 13f; setTextColor(Color.parseColor("#1F3D2E"))
        }
        row.addView(name, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        val add = TextView(ctx).apply {
            text = "+"; textSize = 22f; setTextColor(Color.WHITE)
            gravity = Gravity.CENTER; setBackgroundResource(R.drawable.bg_fgm_button)
            contentDescription = "Thêm ${food.name}"
            setOnClickListener {
                parentFragmentManager.setFragmentResult(LinhDataStore.RESULT_FOOD,
                    Bundle().apply { putString("food_id", food.id) })
                dismiss()
            }
        }
        val px40 = (40 * resources.displayMetrics.density).toInt()
        row.addView(add, LinearLayout.LayoutParams(px40, px40))
        val margin = (5 * resources.displayMetrics.density).toInt()
        row.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = margin }
        return row
    }
}
