package com.example.auticlever.presenter.recordingdetail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.auticlever.R
import com.example.auticlever.databinding.FragmentRecordingDetailBinding
import com.example.auticlever.presenter.main.MainFragment

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding

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


        binding.tvDelete.setOnClickListener{
            DeleteDialog()
        }
        binding.tvSave.setOnClickListener{
            SaveDialog()
        }
        binding.btnBack.setOnClickListener{
            LeaveDialog()
        }

        binding.checkPinning.setOnClickListener{
            if (binding.checkPinning.isChecked) {
                binding.checkPinning.setText(R.string.unpinning)
                binding.checkPinning.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                binding.etMemo.visibility = View.GONE
                binding.scrollViewMemo.visibility = View.VISIBLE
            }
            else {
                binding.checkPinning.setText(R.string.pinning)
                binding.checkPinning.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
                binding.etMemo.visibility  = View.VISIBLE
                binding.scrollViewMemo.visibility = View.GONE
            }
        }

        binding.btnEdit.setOnClickListener{
            binding.etTitleKeyword.requestFocus()

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etTitleKeyword, InputMethodManager.SHOW_IMPLICIT)
        }



        return binding.root
    }

    fun fragmentleave() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()
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

    private fun LeaveDialog() {
        val SaveDialog =
            com.example.auticlever.presenter.recordingdetail.LeaveDetailDialog(requireContext(), this)
        SaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        SaveDialog.show()
    }

}