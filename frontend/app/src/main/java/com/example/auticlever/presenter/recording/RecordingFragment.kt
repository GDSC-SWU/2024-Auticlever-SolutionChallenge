package com.example.auticlever.presenter.recording

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.auticlever.R
import com.example.auticlever.adapter.RecordingPagerAdapter
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConversationUploadDto
import com.example.auticlever.databinding.FragmentRecordingBinding
import com.example.auticlever.presenter.main.MainFragment
import com.example.auticlever.presenter.recordingdetail.RecordingDetailFragment
import com.example.auticlever.presenter.recordloading.RecordLoadingFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingFragment : Fragment() {

    lateinit var binding : FragmentRecordingBinding
    private lateinit var soundVisualizerView: SoundVisualizerView

    private var mediaRecorder: MediaRecorder? = null
    private var recordedFile: File? = null
    private var isRecording = false

    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var recordingStartTime: Long = 0
    private val handler = Handler()

    private val getConversationUploadService = ApiPool.getConversationUpload


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private val recordingRunnable = object : Runnable {
        override fun run() {
            updateRecordingTime()
            handler.postDelayed(this, 100) // 업데이트 간격 (밀리초)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingBinding.inflate(inflater)

        setUpViewPager()

        soundVisualizerView = binding.viewRecording

        soundVisualizerView.onRequestCurrentAmplitude = {
            mediaRecorder?.maxAmplitude ?: 0
        }

        binding.tvDelete.setOnClickListener{
            DeleteDialog()
        }
        binding.ibRecordingArrow.setOnClickListener{
            DeleteDialog()
        }
        binding.tvSave.setOnClickListener{
            SaveDialog()
        }
        binding.ibRecording.setOnClickListener{
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }

        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!permissionToRecordAccepted) {
                    // 사용자가 권한을 거부한 경우
                    activity?.finish()
                    Log.d("Fail", "권한 거부")
                } else {
                    // 권한을 허용한 경우
                    Log.d("Success", "권한 허용")
                }
            }
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(283000)

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            recordedFile = File(context?.cacheDir, "recording_${timeStamp}.mp3")
            setOutputFile(recordedFile!!.absolutePath)

            try {
                Log.d("Start", "녹음 시작")
                prepare()
                start()
                recordingStartTime = SystemClock.elapsedRealtime()
                handler.postDelayed(recordingRunnable, 100)
                soundVisualizerView.clearVisualization()
                soundVisualizerView.startVisualizing(false)
                isRecording = true
                binding.ibRecording.setBackgroundResource(R.drawable.recording_stop)
                binding.recordingBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.systemred))
                Log.d("TAG", "파일 저장 경로: $recordedFile")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("Error", "파일 저장 실패")
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
            handler.removeCallbacks(recordingRunnable)
            soundVisualizerView.stopVisualizing()
            isRecording = false
            binding.ibRecording.setBackgroundResource(R.drawable.recording_start)
            binding.tvRecordingTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray4))
            binding.recordingBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray3))
            Log.d("Stop", "녹음 중단")
        }
        mediaRecorder = null
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecording()
        }
    }

    private fun outLoading(){
        val loadingFragment = requireActivity().supportFragmentManager.findFragmentByTag("RecordLoadingFragment")
        loadingFragment?.let {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }

    private fun conversationUploadApi(file: File) {
        val fileRequestBody = file.asRequestBody("audio/*".toMediaTypeOrNull())

        // 파일 이름을 추출
        val fileName = file.name

        // 파일 파트를 생성
        val filePart = MultipartBody.Part.createFormData("file", fileName, fileRequestBody)

        getConversationUploadService.getConversationUpload(filePart)
            .enqueue(object : Callback<ConversationUploadDto> {
                override fun onResponse(
                    call: Call<ConversationUploadDto>,
                    response: Response<ConversationUploadDto>
                ) {
                    if (response.isSuccessful) {
                        outLoading()
                        val recordingDetailFragment = RecordingDetailFragment()
                        val responseBody = response.body()
                        responseBody?.let {
                            Log.d("success", "업로드 성공")
                            val bundle = Bundle().apply {
                                putInt("conversationID", it.conversationId)
                                Log.d("conversationID", it.conversationId.toString())
                            }
                            recordingDetailFragment.arguments = bundle
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(binding.fragmentContainer.id, recordingDetailFragment)
                            .commit()

                    } else {
                        ErrorDialog()
                        Log.d("error", "실패한 응답 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ConversationUploadDto>, t: Throwable) {
                    t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
                }
            })
    }


    private fun updateRecordingTime() {
        val elapsedMillis = SystemClock.elapsedRealtime() - recordingStartTime
        val seconds = (elapsedMillis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val milliseconds = (elapsedMillis % 1000).toInt()

        binding.tvRecordingTime?.text = String.format("%02d:%02d.%02d", minutes, remainingSeconds, milliseconds)
    }


    fun fragmentdelete() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()
    }

    fun fragmentsave() {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainer.id, RecordLoadingFragment(), "RecordLoadingFragment")
            .commit()
        recordedFile?.let { file ->
            conversationUploadApi(file)
        }
    }

    fun error() {
        recordedFile?.let { file ->
            conversationUploadApi(file)
        }
    }

    private fun setUpViewPager() {

        var adapter = RecordingPagerAdapter(childFragmentManager)
        adapter.addFragment(KeywordsFragment(), "keyword")
        adapter.addFragment(ConsultationFragment(), "consultation")

        binding.viewPager!!.adapter = adapter
        binding.tabLayout!!.setupWithViewPager(binding.viewPager)
    }

    private fun DeleteDialog() {
        val DeleteDialog = DeleteDialog(requireContext(), this)
        DeleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        DeleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        DeleteDialog.show()
    }

    private fun SaveDialog() {
        val SaveDialog = SaveDialog(requireContext(), this)
        SaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        SaveDialog.show()
    }

    private fun ErrorDialog() {
        val ErrorDialog = ErrorDialog(requireContext(), this)
        ErrorDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ErrorDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        ErrorDialog.show()
    }

}
