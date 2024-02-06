package com.example.auticlever.presenter.recordloading

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R
import com.example.auticlever.databinding.DialogLoadingExitBinding

class ExitDialog(context: Context, private val recordingLoadingfragment: com.example.auticlever.presenter.recordloading.RecordLoadingFragment) : Dialog(context) {
    private lateinit var binding : DialogLoadingExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogLoadingExitBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnCancle.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        binding.btnExit.setOnClickListener{
            recordingLoadingfragment.activity?.finishAffinity()
        }
    }

    private fun windowSize() {
        val windowParams = window?.attributes
        windowParams?.width = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        windowParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_exit_height)
        window?.attributes = windowParams
    }
}