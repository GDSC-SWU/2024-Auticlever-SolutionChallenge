package com.example.auticlever.presenter.recordingdetail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
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
import com.example.auticlever.data.dto.ConversationDeleteDto
import com.example.auticlever.data.dto.ConversationMemoDto
import com.example.auticlever.databinding.FragmentRecordingDetailBinding
import com.example.auticlever.presenter.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordingDetailFragment : Fragment() {

    lateinit var binding : FragmentRecordingDetailBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var playbackPosition: Int = 0
    private lateinit var handler: Handler
    private var isFragmentActive: Boolean = false

    private val getConversationDataService = ApiPool.getConversationData
    private val getConversationFileService = ApiPool.getConversationFile
    private val postConversationMemoService = ApiPool.postConversationMemo
    private val deleteConversationService = ApiPool.deleteConversation

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

        isFragmentActive = true
        handler = Handler()

        playFile()
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
            CheckPlaying(mediaPlayer)
        }

        binding.btnTimeago.setOnClickListener{
            playbackPosition = mediaPlayer.currentPosition
            val newPosition = playbackPosition - 10000 // 10초 전 위치로 이동

            if (newPosition < 0) {
                mediaPlayer.seekTo(0)
            } else {
                mediaPlayer.seekTo(newPosition)
            }
        }

        binding.btnTimelater.setOnClickListener{
            playbackPosition = mediaPlayer.currentPosition
            val duration = mediaPlayer.duration
            val newPosition = playbackPosition + 10000 // 10초 후 위치로 이동

            if (newPosition > duration) {
                mediaPlayer.seekTo(duration)
            } else {
                mediaPlayer.seekTo(newPosition)
            }
        }

        MemoSame()

        return binding.root
    }

    fun fragmentleave() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()
    }

    fun saveMemo() {
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1
        postConversationMemo(conversationId, binding.etMemo.text.toString())
    }

    fun delete() {
        val conversationId = arguments?.getInt("conversationId", -1) ?: -1
        deleteConversation(conversationId)
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

            binding.seekbar.max = mediaPlayer.duration

            handler.postDelayed(object : Runnable {
                override fun run() {
                    playbackPosition = mediaPlayer.currentPosition
                    binding.seekbar.progress = playbackPosition
                    handler.postDelayed(this, 1000)
                }
            }, 0)

            updateMediaTime(mediaPlayer)
            TotalMediaTime(mediaPlayer)
        } else {
            mediaPlayer.pause()
            playbackPosition = mediaPlayer.currentPosition
        }
    }

    private fun updateMediaTime(mediaPlayer: MediaPlayer) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isFragmentActive || activity == null) {
                    // 프래그먼트가 화면에서 벗어나면서 handler가 아직 실행 중인 경우 처리
                    handler.removeCallbacksAndMessages(null)
                    return
                }

                playbackPosition = mediaPlayer.currentPosition

                // 밀리초를 분과 초로 변환하여 TextView에 표시합니다.
                val minutes = TimeUnit.MILLISECONDS.toMinutes(playbackPosition.toLong())
                val seconds = TimeUnit.MILLISECONDS.toSeconds(playbackPosition.toLong()) % 60
                val currentTimeStr = String.format("%02d:%02d", minutes, seconds)
                binding.tvStartTime.text = currentTimeStr

                // 주기적으로 업데이트를 반복합니다.
                handler.postDelayed(this, 1000) // 1초마다 업데이트
            }
        }, 0)
    }

    private fun TotalMediaTime(mediaPlayer: MediaPlayer) {
        val totalTime = mediaPlayer.duration

        // 밀리초를 분과 초 형식으로 변환하여 TextView에 표시합니다.
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong()) % 60
        val totalTimeStr = String.format("%02d:%02d", minutes, seconds)
        binding.tvEndTime.text = totalTimeStr
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
    }

    fun postConversationMemo(conversationId: Int, memo: String) {
        val call = postConversationMemoService.postConversationMemo(conversationId, memo)
        call.enqueue(object : Callback<ConversationMemoDto> {
            override fun onResponse(call: Call<ConversationMemoDto>, response: Response<ConversationMemoDto>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    Log.d("message", message.toString())
                } else {
                    Log.d("error", "실패한 응답 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ConversationMemoDto>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }

    fun deleteConversation(conversationId: Int) {
        val call = deleteConversationService.deleteConversation(conversationId)
        call.enqueue(object : Callback<ConversationDeleteDto> {
            override fun onResponse(call: Call<ConversationDeleteDto>, response: Response<ConversationDeleteDto>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    Log.d("message", message.toString())
                } else {
                    Log.d("error", "실패한 응답 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ConversationDeleteDto>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFragmentActive = false
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}