package com.example.auticlever.presenter.recording

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import com.example.auticlever.R
import com.example.auticlever.databinding.DialogRecordingSaveBinding
import com.example.auticlever.presenter.recordloading.RecordLoadingFragment

class SaveDialog(context: Context, private val recordingfragment: RecordingFragment) : Dialog(context) {
    private lateinit var binding : DialogRecordingSaveBinding
    lateinit var cancleBtn : Button
    lateinit var SaveBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogRecordingSaveBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        cancleBtn = binding.btnCancle
        SaveBtn = binding.btnSave

        cancleBtn.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        SaveBtn.setOnClickListener{
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