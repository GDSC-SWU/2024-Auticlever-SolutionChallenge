package com.example.auticlever.presenter.recordingdetail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.auticlever.databinding.FragmentRecordingDetailBinding

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding
    lateinit var deleteTextView: TextView
    lateinit var SaveTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingDetailBinding.inflate(inflater)

        deleteTextView = binding.tvDelete
        SaveTextView = binding.tvSave

        deleteTextView.setOnClickListener{
            DeleteDialog()
        }
        SaveTextView.setOnClickListener{
            SaveDialog()
        }


        return binding.root
    }

    private fun DeleteDialog() {
        val DeleteDialog =
            com.example.auticlever.presenter.recordingdetail.DeleteDetailDialog(requireContext(), this)
        DeleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        DeleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        DeleteDialog.show()
    }

    private fun SaveDialog() {
        val SaveDialog =
            com.example.auticlever.presenter.recordingdetail.SaveDetailDialog(requireContext(), this)
        SaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        SaveDialog.show()
    }

}