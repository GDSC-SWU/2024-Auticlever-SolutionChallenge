package com.example.auticlever.presenter.recordingdetail

import android.content.Context
import android.graphics.Color
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
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.auticlever.R
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConversationData
import com.example.auticlever.databinding.FragmentRecordingDetailBinding
import com.example.auticlever.presenter.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var playbackPosition: Int = 0
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
        playFile()

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
            CheckPlaying(mediaPlayer)
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

    private fun CheckPlaying(mediaPlayer: MediaPlayer) {
        if (binding.checkPlay.isChecked) {
            mediaPlayer.seekTo(playbackPosition)
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
            playbackPosition = mediaPlayer.currentPosition
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


    private fun createMP3File(inputStream: InputStream): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mp3File = File(context?.cacheDir, "recording_${timeStamp}.mp3")
        mp3File.outputStream().use { fileOut ->
            inputStream.use { input ->
                input.copyTo(fileOut)
            }
        }
        Log.d("FileCreation", "MP3 파일이 생성되었습니다. 경로: ${mp3File.absolutePath}")

        return mp3File
    }

    private fun playFile() {
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1
        if (conversationId != -1) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = getConversationFileService.getConversationFile(conversationId).execute()
                    if (response.isSuccessful) {
                        val mp3Data = response.body()?.byteStream()
                        mp3Data?.let {
                            val file = createMP3File(it)
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(file.path)
                                prepare()
                            }
                        }
                    } else {
                        Log.d("error", "실패한 응답")
                    }
                } catch (e: Exception) {
                    Log.e("error", "네트워크 호출 오류: ${e.message}")
                }
            }
        }
    }


    private fun getConversationDataApi() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 대화 데이터 가져오기
                val conversationData = getConversationData()

                // 대화 데이터를 UI에 설정
                setupConversationData(conversationData)
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            }
        }
    }

    private suspend fun getConversationData(): ConversationData {
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1
        return withContext(Dispatchers.IO) {
            getConversationDataService.getConversationData(conversationId).execute().body()!!
        }
    }

    private fun setupConversationData(conversationData: ConversationData) {
        if (conversationData.cvMemo_data.firstOrNull()?.content != null) {
            binding.etMemo.text = Editable.Factory.getInstance().newEditable(conversationData.cvMemo_data.firstOrNull()?.content)
        } else {
            binding.etMemo.hint = resources.getString(R.string.none_memo)
            binding.etBottomMemo.hint = resources.getString(R.string.none_memo)
        }
        binding.etTitleKeyword.text = Editable.Factory.getInstance().newEditable(conversationData.conversation_data.keyword)
        binding.tvAiSummarize.text = conversationData.conversation_data.summary
        binding.tvRecordingContent.text = conversationData.conversation_data.content
        binding.tvDate.text = conversationData.conversation_data.date

        //playFile()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}