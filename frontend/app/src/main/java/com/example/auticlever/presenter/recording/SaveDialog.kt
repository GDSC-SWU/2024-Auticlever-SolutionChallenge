package com.example.auticlever.presenter.recording

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R
import com.example.auticlever.databinding.DialogRecordingSaveBinding

class SaveDialog(context: Context, private val recordingfragment: RecordingFragment) : Dialog(context) {
    private lateinit var binding : DialogRecordingSaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogRecordingSaveBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnCancle.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        binding.btnSave.setOnClickListener{
            recordingfragment.fragmentsave()
            dismiss()
        }
    }

    private fun windowSize() {
        val windowParams = window?.attributes
        windowParams?.width = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        windowParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_height)
        window?.attributes = windowParams
    }
}