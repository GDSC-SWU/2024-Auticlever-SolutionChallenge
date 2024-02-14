package com.example.auticlever.presenter.recordingdetail

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.auticlever.R
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConversationData
import com.example.auticlever.databinding.FragmentRecordingDetailBinding
import com.example.auticlever.presenter.main.MainFragment
import retrofit2.Call
import retrofit2.Response

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding
    private val getConversationDataService = ApiPool.getConversationData

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

        getConversationDataApi()

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
            CheckPinning()
        }

        binding.btnEdit.setOnClickListener{
            KeyboardUp()
        }

        MemoSame()
        bottomMemoHeight()

        return binding.root
    }

    fun bottomMemoHeight() {
        binding.etBottomMemo.post {
            val editTextHeight = binding.etBottomMemo.height
            val maxHeight = resources.getDimensionPixelSize(R.dimen.max_edit_text_height)
            if (editTextHeight > maxHeight) {
                val layoutParams = binding.etBottomMemo.layoutParams
                layoutParams.height = maxHeight
                binding.etBottomMemo.layoutParams = layoutParams
            }
        }
    }

    fun fragmentleave() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()
    }

    private fun DeleteDialog() {
        val DeleteDialog =
            DeleteDetailDialog(requireContext(), this)
        DeleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        DeleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        DeleteDialog.show()
    }

    private fun SaveDialog() {
        val SaveDialog =
            SaveDetailDialog(requireContext(), this)
        SaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        SaveDialog.show()
    }

    private fun LeaveDialog() {
        val LeaveDialog =
            LeaveDetailDialog(requireContext(), this)
        LeaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        LeaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        LeaveDialog.show()
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

    private fun KeyboardUp() {
        binding.etTitleKeyword.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitleKeyword, InputMethodManager.SHOW_IMPLICIT)
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



    private fun getConversationDataApi() {
        getConversationDataService.getConversationData(2).enqueue(object : retrofit2.Callback<ConversationData> {
            override fun onResponse(
                call: Call<ConversationData>, response: Response<ConversationData>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.etTitleKeyword.text = Editable.Factory.getInstance().newEditable(it.conversation_data.keyword)
                        binding.tvAiSummarize.text = it.conversation_data.summary
                        binding.tvRecordingContent.text = it.conversation_data.content
                        binding.tvDate.text = it.conversation_data.date
                        binding.etMemo.text = Editable.Factory.getInstance().newEditable(it.cvMemo_data.firstOrNull()?.content)
                    }
                } else {
                    Log.d("error", "실패한 응답")
                }
            }
            override fun onFailure(call: Call<ConversationData>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }
}