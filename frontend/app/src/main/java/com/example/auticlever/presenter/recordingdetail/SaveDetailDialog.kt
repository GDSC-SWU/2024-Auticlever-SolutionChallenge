package com.example.auticlever.presenter.recordingdetail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R
import com.example.auticlever.databinding.DialogRecordingDetailSaveBinding


class SaveDetailDialog(context: Context, private val recordingDetailfragment: RecordingDetailFragment) : Dialog(context) {
    private lateinit var binding : DialogRecordingDetailSaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogRecordingDetailSaveBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnCancle.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        binding.btnSave.setOnClickListener{
            recordingDetailfragment.fragmentleave()
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