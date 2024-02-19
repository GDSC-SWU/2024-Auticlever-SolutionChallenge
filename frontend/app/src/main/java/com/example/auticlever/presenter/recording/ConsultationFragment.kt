package com.example.auticlever.presenter.recording

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auticlever.adapter.RecordingConsultListAdapter
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConsultListDto
import com.example.auticlever.data.dto.MainMemoDto
import com.example.auticlever.databinding.FragmentConsultationBinding
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class ConsultationFragment : Fragment() {

    lateinit var binding : FragmentConsultationBinding
    private val getConsultList = ApiPool.getConsultList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConsultationBinding.inflate(inflater)

        getMainMemoApi()
        getConsultListApi()

        return binding.root
    }

    private fun getMainMemoApi() {
        ApiPool.getMainMemo.getMainMemo().enqueue(object : retrofit2.Callback<MainMemoDto> {
            override fun onResponse(
                call: Call<MainMemoDto>, response: Response<MainMemoDto>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        binding.tvConsultRecording.setText(it.content)
                        Log.d("success", "성공")
                    }
                } else {
                    Log.d("error", "실패한 응답")
                }
            }

            override fun onFailure(call: Call<MainMemoDto>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }

    private fun getConsultListApi() {
        getConsultList.getConsultList().enqueue(object : retrofit2.Callback<ConsultListDto> {
            override fun onResponse(
                call: Call<ConsultListDto>, response: Response<ConsultListDto>
            ) {
                if (response.isSuccessful) {
                    val adapter = RecordingConsultListAdapter()
                    binding.rvConversation.adapter = adapter

                    response.body()?.let {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        adapter.submitList(it.consultations.sortedByDescending {
                            dateFormat.parse(it.date)
                        })
                    }
                } else {
                    Log.d("error", "서버 응답 실패. HTTP상태코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ConsultListDto>, t: Throwable) {
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