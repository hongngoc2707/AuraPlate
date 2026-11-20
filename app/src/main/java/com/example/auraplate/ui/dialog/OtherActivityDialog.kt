package com.example.auraplate.ui.dialog

import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.auraplate.R
import com.example.auraplate.ui.linh.LinhDataStore

/** Overlay_HoatDongKhac: nhập tên, trả lại màn hình Thêm vận động. */
class OtherActivityDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_other_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.otherClose).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.otherCancel).setOnClickListener { dismiss() }
        val name = view.findViewById<EditText>(R.id.otherNameInput)
        view.findViewById<View>(R.id.otherConfirm).setOnClickListener {
            val value = name.text.toString().trim()
            if (value.isBlank()) {
                name.error = "Vui lòng nhập tên hoạt động"
                Toast.makeText(requireContext(), "Chưa nhập tên hoạt động", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.setFragmentResult(LinhDataStore.RESULT_OTHER_ACTIVITY,
                    Bundle().apply { putString("exercise_name", value) })
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90f).toInt()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setDimAmount(0.35f)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}
