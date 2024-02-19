package com.example.auticlever.presenter.consultingdetail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R
import com.example.auticlever.data.ApiPool.deleteConsultDetail
import com.example.auticlever.databinding.DialogRecordingDetailDeleteBinding

class DeleteDetailDialog(context: Context, private val consultingDetailfragment: com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment) : Dialog(context) {
    private lateinit var binding: DialogRecordingDetailDeleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogRecordingDetailDeleteBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnCancle.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        binding.btnDelete.setOnClickListener {
            consultingDetailfragment.deleteConsultDetail()
            consultingDetailfragment.goList()
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