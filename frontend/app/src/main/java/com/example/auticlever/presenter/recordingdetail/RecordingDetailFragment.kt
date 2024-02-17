package com.example.auticlever.presenter.recordingdetail

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.example.auticlever.R
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConversationData
import com.example.auticlever.databinding.FragmentRecordingDetailBinding
import com.example.auticlever.presenter.main.MainFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding
    private lateinit var mediaPlayer: MediaPlayer
    private val getConversationDataService = ApiPool.getConversationData
    private val getConversationFileService = ApiPool.getConversationFile

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

        binding.checkPlay.setOnClickListener{
            CheckPlaying()
        }

        MemoSame()

        return binding.root
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
            binding.etBottomMemo.visibility = View.VISIBLE
        }
        else {
            binding.checkPinning.setText(R.string.pinning)
            binding.checkPinning.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
            binding.etMemo.visibility  = View.VISIBLE
            binding.etBottomMemo.visibility = View.GONE
        }
    }

    private fun CheckPlaying() {
        if (binding.checkPlay.isChecked) {

        }
        else {
            //playFile()
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
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1

        if (conversationId != null) {
            getConversationDataService.getConversationData(conversationId).enqueue(object : retrofit2.Callback<ConversationData> {
                override fun onResponse(
                    call: Call<ConversationData>, response: Response<ConversationData>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.cvMemo_data.firstOrNull()?.content != null) {
                                binding.etMemo.text = Editable.Factory.getInstance().newEditable(it.cvMemo_data.firstOrNull()?.content)
                            } else {
                                binding.etMemo.hint = resources.getString(R.string.none_memo)
                                binding.etBottomMemo.hint = resources.getString(R.string.none_memo)
                            }
                            binding.etTitleKeyword.text = Editable.Factory.getInstance().newEditable(it.conversation_data.keyword)
                            binding.tvAiSummarize.text = it.conversation_data.summary
                            binding.tvRecordingContent.text = it.conversation_data.content
                            binding.tvDate.text = it.conversation_data.date
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
        else {
            Log.d("error", "ID가 널 값")
        }
    }

    private fun createMP3File(inputStream: InputStream): File {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mp3File = File(downloadDir, "recording_${timeStamp}.mp3")
//        mp3File.outputStream().use { fileOut ->
//            inputStream.use { input ->
//                input.copyTo(fileOut)
//            }
//        }
        return mp3File
    }

    private fun playFile() {
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1
        if (conversationId != null) {
            val call = getConversationFileService.getConversationFile(conversationId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val mp3Data = response.body()?.byteStream()
                        mp3Data?.let {
                            val file = createMP3File(it)
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(file.path)
                                prepareAsync()
                                setOnPreparedListener {
                                    start()
                                }
                            }
                        }
                    } else {
                        Log.d("error", "실패한 응답")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}