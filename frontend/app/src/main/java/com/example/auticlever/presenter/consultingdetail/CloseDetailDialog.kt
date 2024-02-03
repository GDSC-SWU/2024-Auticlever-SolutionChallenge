package com.example.auticlever.presenter.consultingdetail

import com.example.auticlever.presenter.recordingdetail.RecordingDetailFragment
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import com.example.auticlever.R

class CloseDetailDialog(context: Context, private val closeDetailFragment: ConsultingDetailFragment) : Dialog(context) {
    private lateinit var binding : DialogConsultDetailCloseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        binding = DialogRecordingDetailLeaveBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        windowSize()

        binding.btnCancle.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
        binding.btnDelete.setOnClickListener{
            recordingDetailfragment.fragmentleave()
            dismiss()
        }
    }

    private fun windowSize() {
        val windowParams = window?.attributes
        windowParams?.width = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_width)
        windowParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_dialog_leave_height)
        window?.attributes = windowParams
    }
}