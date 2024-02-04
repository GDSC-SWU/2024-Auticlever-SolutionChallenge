package com.example.auticlever.presenter.consultingdetail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.example.auticlever.R
import com.example.auticlever.databinding.FragmentConsultingDetailBinding
import com.example.auticlever.presenter.main.MainFragment

class ConsultingDetailFragment : Fragment() {
    lateinit var binding : FragmentConsultingDetailBinding
    lateinit var callback: OnBackPressedCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConsultingDetailBinding.inflate(inflater)


        binding.tvDelete.setOnClickListener{
            DeleteDialog()
        }
        binding.tvSave.setOnClickListener{
            SaveDialog()
        }
        binding.btnBack.setOnClickListener{
            LeaveDialog()
        }

        binding.btnEdit.setOnClickListener{
            KeyboardUp()
        }

        binding.ibTip.setOnClickListener{
            TipDialog()
        }

        binding.checkPinning.setOnClickListener{
            CheckPinning()
        }
        MemoSame()

        return binding.root
    }

    private fun DeleteDialog() {
        val deleteDialog =
            com.example.auticlever.presenter.consultingdetail.DeleteDetailDialog(requireContext(), this)
        deleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.show()
    }

    private fun SaveDialog() {
        val saveDialog =
            com.example.auticlever.presenter.consultingdetail.SaveDetailDialog(requireContext(), this)
        saveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        saveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        saveDialog.show()
    }

    private fun LeaveDialog() {
        val leaveDialog =
            com.example.auticlever.presenter.consultingdetail.LeaveDetailDialog(requireContext(), this)
        leaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        leaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        leaveDialog.show()
    }

    fun fragmentremove() {
        // 뒤로가기 클릭시 현재 fragment를 삭제
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    private fun KeyboardUp() {
        binding.etTitleKeyword.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitleKeyword, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun TipDialog(){
        val tipDialog = com.example.auticlever.presenter.consultingdetail.TipDetailDialog(requireContext(), this)
        tipDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tipDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        tipDialog.show()

    }
    private fun CheckPinning() {
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

    private fun MemoSame() {
        binding.etMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etBottomMemo.text.toString() != s.toString()) {
                    binding.etBottomMemo.setText(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etBottomMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.etMemo.text.toString() != s.toString()) {
                    binding.etMemo.setText(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}