package com.example.auticlever.presenter.consultingdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
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
import com.example.auticlever.data.ApiPool.getConsultData
import com.example.auticlever.data.api.ConsultCsMemoApiFactory
import com.example.auticlever.data.api.ConsultUploadApiFactory
import com.example.auticlever.data.dto.ConsultCsMemoDto
import com.example.auticlever.data.dto.ConsultCsMemoResponseDto
import com.example.auticlever.data.dto.ConsultDataDto
import com.example.auticlever.data.dto.ConsultUploadDto
import com.example.auticlever.data.dto.ConversationData
import com.example.auticlever.data.dto.ErrorDto
import com.example.auticlever.databinding.FragmentConsultingDetailBinding
import com.example.auticlever.presenter.consultloading.ConsultLoadingFragment
import com.example.auticlever.presenter.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
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
                var selectedFileUri = result.data?.data
                Log.d("FilePicker", "Selected File URI: $selectedFileUri")
                selectedFileUri?.let { uploadMp3File(it) }
            }
        }
    private var consultationId: String? = null
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
        getConsultDataApi()

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
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.show()
    }

    private fun saveDialog() {
        val saveDialog =
            com.example.auticlever.presenter.consultingdetail.SaveDetailDialog(
                requireContext(),
                this
            )
        saveDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        saveDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        saveDialog.show()
    }

    private fun leaveDialog() {
        val leaveDialog =
            com.example.auticlever.presenter.consultingdetail.LeaveDetailDialog(
                requireContext(), this)
        leaveDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        leaveDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        leaveDialog.show()
    }

    fun goMain() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment())
            .commit()
    }

    private fun goLoading() {
        val loadingFragment = requireActivity().supportFragmentManager.findFragmentByTag("ConsultLoadingFragment")
        if (loadingFragment == null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ConsultLoadingFragment(), "ConsultLoadingFragment")
                .commit()
        }
    }
    private fun showContents(){
        //파일 업로드 버튼과 점선 안보이게하기
        binding.ivUploadBackground.visibility = View.GONE
        binding.btnConsultDetailUpload.visibility = View.GONE
        //재생바,메모,ai본문 및 요약내용 보이게하기
        binding.bottomAppbar.visibility = View.VISIBLE
        binding.scrollViewMemo.visibility = View.VISIBLE
        binding.tvAiSummarizeTitle.visibility = View.VISIBLE
        binding.tvAiSummarize.visibility = View.VISIBLE
        binding.tvConsultingContentTitle.visibility = View.VISIBLE
        binding.tvConsultingContent.visibility = View.VISIBLE
    }
    private fun outLoading(){
        val loadingFragment = requireActivity().supportFragmentManager.findFragmentByTag("ConsultLoadingFragment")
        loadingFragment?.let {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }


     private fun keyboardUp() {
        binding.etTitle.requestFocus()

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etTitle, InputMethodManager.SHOW_IMPLICIT)
    }

     private fun tipDialog() {
        val tipDialog = com.example.auticlever.presenter.consultingdetail.TipDetailDialog(
            requireContext(),
            this
        )
        tipDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tipDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
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

    //처음값 저장
     /*private fun setConsultationDetails(title: String, mainMemo: String, csMemo: String) {
        binding.etTitleKeyword.setText(title)
        binding.tvMainMemo.text = mainMemo
        binding.etMemo.setText(csMemo)
    }*/

    // 수정값 저장하기
    fun saveCsMemoDetails() {
        val consultationId = arguments?.getInt("conversationId", -1) ?: -1
        val title = binding.etTitle.text.toString()
        val csMemo = binding.etMemo.text.toString()
        val mainMemo = binding.tvMainMemo.text.toString()

        if (consultationId != null) {
            ConsultCsMemoApiFactory.consultCsMemoApiService.sendConsultCsMemoData(consultationId,title, csMemo, mainMemo)
                .enqueue(object : Callback<ConsultCsMemoResponseDto> {
                    override fun onResponse(
                        call: Call<ConsultCsMemoResponseDto>,
                        response: Response<ConsultCsMemoResponseDto>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                Log.d("작성 및 수정 성공", "csMemo Message: ${it.csMemoMessage}, mainMemo Message: ${it.mainMemoMessage}")
                            }
                        } else {
                            // Handle the case where the server response is not successful
                            Log.d("error", "서버 응답 실패. HTTP상태코드: ${response.code()}")
                            // Check if it's a validation error and log the details
                            val errorBody = response.errorBody()?.string()
                            Log.d("error", "Error Body: $errorBody")
                        }
                    }

                    override fun onFailure(
                        call: Call<ConsultCsMemoResponseDto>,
                        t: Throwable
                    ) {
                        if (call.isCanceled) {
                            // Handle the case where the server request is canceled
                            Log.d("error", "서버 요청 취소")
                        } else {
                            // Handle network error or exception
                            Log.d("error", "네트워크 오류 또는 예외 발생: ${t.message}")
                        }
                    }
                })
        } else {
            Log.d("error", "consultationId가 null입니다. 처리가 필요합니다.")
        }
    }

    // 존재하는 값 가져오기
    /*private fun loadConsultationDetails() {
        // consultationId가 null이 아닌 경우에만 서버 요청을 수행
        if (consultationId != null) {
            ConsultCsMemoApiFactory.consultCsMemoApiService.getConsultationDetails(consultationId)
                .enqueue(object : Callback<ConsultCsMemoRequestDto> {
                    override fun onResponse(
                        call: Call<ConsultCsMemoRequestDto>,
                        response: Response<ConsultCsMemoRequestDto>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                // 서버 응답이 성공적인 경우
                                setConsultationDetails(it.title, it.csMemo, it.mainMemo)
                            }
                        } else {
                            // 서버 응답이 실패한 경우
                            Log.d("error", "서버 응답 실패. HTTP상태코드: ${response.code()}")
                        }
                    }

                    override fun onFailure(
                        call: Call<ConsultCsMemoRequestDto>,
                        t: Throwable
                    ) {
                        if (call.isCanceled) {
                            // 서버 요청이 취소된 경우
                            Log.d("error", "서버 요청 취소")
                        } else {
                            // 네트워크 오류 또는 예외가 발생한 경우
                            Log.d("error", "네트워크 오류 또는 예외 발생: ${t.message}")
                        }
                    }
                })
        } else {
            // consultationId가 null인 경우 에러 메시지 출력
            Log.e("error", "consultationId is null. Cannot load consultation details.")
        }
    }*/
    fun extractDate(dateField: String): String? {
        val dateRegex = """(\d{4}-\d{2}-\d{2})""".toRegex()
        val matchResult = dateRegex.find(dateField)

        return matchResult?.let {
            val datePart = it.groupValues[1]
            val timePart = it.groupValues[2]
            "$datePart $timePart"
        }
    }
    private fun getConsultDataApi() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 대화 데이터 가져오기
                val consultData = getConsultData()

                // 대화 데이터를 UI에 설정
                setupConsultData(consultData)
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            }
        }
    }

    private suspend fun getConsultData(): ConsultDataDto {
        val consultationId = arguments?.getInt("consultationId", -1) ?: -1
        return withContext(Dispatchers.IO) {
            getConsultData.getConsultData(consultationId).execute().body()!!
        }
    }

    private fun setupConsultData(consultData: ConsultDataDto) {
        showContents()
        if (consultData.csMemo_data.firstOrNull()?.content != null) {
            binding.etMemo.text = Editable.Factory.getInstance().newEditable(consultData.csMemo_data.firstOrNull()?.content)
        } else {
            binding.etMemo.hint = resources.getString(R.string.none_memo)
            binding.etBottomMemo.hint = resources.getString(R.string.none_memo)
        }
        binding.etTitle.text = Editable.Factory.getInstance().newEditable(consultData.csMemo_data.firstOrNull()?.title)
        binding.tvAiSummarize.text = consultData.consultation_data.summary
        binding.tvConsultingContent.text = consultData.consultation_data.content
        binding.tvDate.text = extractDate(consultData.consultation_data.date)
    }

     private fun clickUploadFile() {

        // 파일 선택 Intent 시작
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
        }
        selectMp3FileLauncher.launch(intent)
        showContents()
    }
     private fun getFileName(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val fileName = it.getString(columnIndex)
            it.close()
            return fileName ?: ""
        } ?: run {
            return ""
        }
    }
     private fun uploadMp3File(fileUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(fileUri)
        val fileRequestBody = inputStream?.readBytes()?.toRequestBody("audio/*".toMediaTypeOrNull())

        fileRequestBody?.let {
            goLoading()
            // 파일 이름 및 확장자를 추출
            val fileName = getFileName(fileUri)

            // 파일 파트를 생성
            val filePart = MultipartBody.Part.createFormData("file", fileName, fileRequestBody)

            // Retrofit을 사용하여 서버에 업로드
            ConsultUploadApiFactory.createConsultUploadApiService().uploadConsultation(filePart)
                .enqueue(object : Callback<ConsultUploadDto> {
                    override fun onResponse(
                        call: retrofit2.Call<ConsultUploadDto>,
                        response: Response<ConsultUploadDto>
                    ) {
                        if (response.isSuccessful) {
                            //로딩페이지 나가기
                            outLoading()
                            val responseBody = response.body()
                            responseBody?.let {
                                // 서버로부터 받은 요약 내용을 TextView에 대입
                                Log.d("요약 완료", "Message: ${responseBody.message}, Summary: ${responseBody.summary}")
                                binding.tvAiSummarize.text = responseBody.summary

                            }
                        } else {
                            // 서버 응답이 실패
                            Log.d("error", "서버 응답 실패. HTTP상태코드: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ConsultUploadDto>, t: Throwable) {
                        if (call.isCanceled) {
                            Log.d("error", "서버 요청 취소")
                        } else {
                            // 네트워크 오류 또는 예외 발생 시 처리
                            Log.d("error", "네트워크 오류 또는 예외 발생: ${t.message}")
                        }
                    }
                })

        }
    }
}
