package com.example.auticlever.presenter.consultinglist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auticlever.R
import com.example.auticlever.adapter.ConsultListAdapter
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConsultListDto
import com.example.auticlever.data.dto.Consultations
import com.example.auticlever.databinding.FragmentConsultingListBinding
import com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment
import com.example.auticlever.presenter.main.MainFragment
import com.google.android.flexbox.FlexboxLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class ConsultingListFragment : Fragment() {
    private val getConsultList = ApiPool.getConsultList
    private lateinit var binding: FragmentConsultingListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConsultingListBinding.inflate(inflater)

        binding.btnCreate.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ConsultingDetailFragment())
                .commit()
        }

        binding.ivLeftArrow.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }

        getConsultListApi()
        return binding.root

    }
    private fun getConsultListApi() {

        getConsultList.getConsultList().enqueue(object : retrofit2.Callback<ConsultListDto> {
            override fun onResponse(
                call: Call<ConsultListDto>, response: Response<ConsultListDto>
            ) {
                if (response.isSuccessful) {
                    val adapter = ConsultListAdapter()
                    binding.rvConsultation.adapter = adapter

                    adapter.setOnItemClickListener(object : ConsultListAdapter.OnItemClickListener {
                        override fun onItemClick(consultations: Consultations) {
                            val consultingDetailFragment = ConsultingDetailFragment()
                            val bundle = Bundle().apply {
                                putInt("consultationId", consultations.consultationId)
                                Log.d("consultaionId", consultations.consultationId.toString())
                            }
                            consultingDetailFragment.arguments = bundle

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, consultingDetailFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    })
                    val responseBody = response.body()
                    responseBody?.let {
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
