package com.example.auticlever.presenter.recording

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auticlever.adapter.KeywordsAdapter
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.KeywordsDto
import com.example.auticlever.databinding.FragmentKeywordsBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import retrofit2.Call
import retrofit2.Response

class KeywordsFragment : Fragment() {

    lateinit var binding : FragmentKeywordsBinding
    private val getKeywords = ApiPool.getKeywords

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeywordsBinding.inflate(inflater)

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        binding.rvKeywords.layoutManager = layoutManager

        getKeywordsApi()
        binding.ibRefresh.setOnClickListener{
            getKeywordsApi()
        }
        binding.tvRefresh.setOnClickListener{
            getKeywordsApi()
        }

        return binding.root
    }

    private fun getKeywordsApi() {
        getKeywords.getKeywords().enqueue(object : retrofit2.Callback<KeywordsDto> {
            override fun onResponse(
                call: Call<KeywordsDto>, response: Response<KeywordsDto>
            ) {
                if (response.isSuccessful) {
                    val adapter = KeywordsAdapter()
                    binding.rvKeywords.adapter = adapter

                    response.body()?.let {
                        adapter.submitList(it.recommended_keywords)
                    }
                } else {
                    Log.d("error", "실패한 응답")
                }
            }

            override fun onFailure(call: Call<KeywordsDto>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }

}