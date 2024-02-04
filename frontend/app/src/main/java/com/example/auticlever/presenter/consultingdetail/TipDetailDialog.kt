package com.example.auticlever.presenter.consultingdetail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R
import com.example.auticlever.databinding.DialogConsultingTipBinding

class TipDetailDialog(context: Context, private val consultingDetailFragment: ConsultingDetailFragment) : Dialog(context) {
    private lateinit var binding : DialogConsultingTipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogConsultingTipBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnClose.setOnClickListener {
            dismiss() // dialog 닫기
        }

    }

    private fun windowSize() {
        val windowParams = window?.attributes
        windowParams?.width = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        windowParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_tip_height)
        window?.attributes = windowParams
    }
}