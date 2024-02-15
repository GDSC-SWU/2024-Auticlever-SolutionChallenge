package com.example.auticlever.presenter.consultingdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.auticlever.R
import com.example.auticlever.data.api.ConsultUploadApiFactory
import com.example.auticlever.data.dto.ConsultUploadDto
import com.example.auticlever.databinding.FragmentConsultingDetailBinding
import com.example.auticlever.presenter.consultloading.ConsultLoadingFragment
import com.example.auticlever.presenter.main.MainFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsultingDetailFragment : Fragment() {
    lateinit var binding: FragmentConsultingDetailBinding
    private val selectMp3FileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri = result.data?.data
                selectedFileUri?.let { uploadMp3File(it) }
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
        binding = FragmentConsultingDetailBinding.inflate(inflater)


        binding.tvDelete.setOnClickListener {
            deleteDialog()
        }
        binding.tvSave.setOnClickListener {
            saveDialog()
        }
        binding.btnBack.setOnClickListener {
            leaveDialog()
        }

        binding.btnEdit.setOnClickListener {
            keyboardUp()
        }

        binding.ibTip.setOnClickListener {
            tipDialog()
        }

        binding.checkPinning.setOnClickListener {
            checkPinning()
        }

        binding.ivUploadBackground.setOnClickListener {
            clickUploadFile()
        }

        binding.btnConsultDetailUpload.setOnClickListener {
            clickUploadFile()
        }

        return binding.root
    }

    private fun deleteDialog() {
        val deleteDialog =
            com.example.auticlever.presenter.consultingdetail.DeleteDetailDialog(
                requireContext(),
                this
            )
        deleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.show()
    }

    private fun saveDialog() {
        val saveDialog =
            com.example.auticlever.presenter.consultingdetail.SaveDetailDialog(
                requireContext(),
                this
            )
        saveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        saveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        saveDialog.show()
    }

    private fun leaveDialog() {
        val leaveDialog =
            com.example.auticlever.presenter.consultingdetail.LeaveDetailDialog(
                requireContext(),
                this
            )
        leaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        leaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        leaveDialog.show()
    }

    fun goMain() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment())
            .commit()
    }

    fun goLoading() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ConsultLoadingFragment())
            .commit()
    }


    private fun keyboardUp() {
        binding.etTitleKeyword.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitleKeyword, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun tipDialog() {
        val tipDialog = com.example.auticlever.presenter.consultingdetail.TipDetailDialog(
            requireContext(),
            this
        )
        tipDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tipDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        tipDialog.show()

    }

    private fun checkPinning() {
        if (binding.checkPinning.isChecked) {
            binding.checkPinning.setText(R.string.unpinning)
            binding.checkPinning.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            binding.etMemo.visibility = View.GONE
            binding.scrollViewMemo.visibility = View.VISIBLE
        } else {
            binding.checkPinning.setText(R.string.pinning)
            binding.checkPinning.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray4
                )
            )
            binding.etMemo.visibility = View.VISIBLE
            binding.scrollViewMemo.visibility = View.GONE
        }
    }

    private fun clickUploadFile() {
        //파일 업로드 버튼과 점선 안보이게하기
        binding.ivUploadBackground.visibility = View.GONE
        binding.btnConsultDetailUpload.visibility = View.GONE
        //재생바,메모,ai본문 및 요약내용 보이게하기
        binding.bottomAppbar.visibility = View.VISIBLE
        binding.scrollViewMemo.visibility = View.VISIBLE
        binding.tvAiSummarizeTitle.visibility = View.VISIBLE
        binding.tvAiSummarize.visibility = View.VISIBLE
        binding.tvRecordingContentTitle.visibility = View.VISIBLE
        binding.tvRecordingContent.visibility = View.VISIBLE

        // 파일 선택 Intent 시작
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/mpeg" // mp3 파일 형식에 따라 변경
        }
        selectMp3FileLauncher.launch(intent)
    }

    private fun uploadMp3File(fileUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(fileUri)
        val fileRequestBody = inputStream?.readBytes()?.toRequestBody("audio/mpeg".toMediaTypeOrNull())

        fileRequestBody?.let {
            val filePart = MultipartBody.Part.createFormData("file", "audio.mp3", it)

            // Retrofit을 사용하여 서버에 업로드
            ConsultUploadApiFactory.createConsultUploadApiService().uploadConsultation(filePart)
                .enqueue(object : Callback<ConsultUploadDto> {
                    override fun onResponse(
                        call: Call<ConsultUploadDto>,
                        response: Response<ConsultUploadDto>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                // 서버로부터 받은 요약 내용을 TextView에 대입
                                Log.d("success","${it.message}")
                                binding.tvAiSummarize.text = it.message
                            }
                        } else {
                            // 서버 응답이 실패한 경우 처리
                            Log.d("error", "서버 응답 실패")
                        }
                    }

                    override fun onFailure(call: Call<ConsultUploadDto>, t: Throwable) {
                        // 네트워크 오류 또는 예외 발생 시 처리
                        Log.d("error", "네트워크 오류 또는 예외 발생: ${t.message}")
                    }
                })
        }

}}
