package com.example.auticlever.presenter.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auticlever.adapter.ConversationListAdapter
import com.example.auticlever.adapter.MainKeywordsAdapter
import com.example.auticlever.data.ApiPool
import com.example.auticlever.data.dto.ConversationListDto
import com.example.auticlever.data.dto.Conversations
import com.example.auticlever.data.dto.KeywordsDto
import com.example.auticlever.databinding.FragmentMainBinding
import com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment
import com.example.auticlever.presenter.consultinglist.ConsultingListFragment
import com.example.auticlever.presenter.recording.RecordingFragment
import com.example.auticlever.presenter.recordingdetail.RecordingDetailFragment
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val getKeywords = ApiPool.getKeywords
    private val getConversationList = ApiPool.getConversationList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)

        binding.btnRecord.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, RecordingFragment())
                .commit()
        }

        binding.tvConsultRecords.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, ConsultingListFragment())
                .commit()
        }

        binding.ivRightArrow.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, ConsultingListFragment())
                .commit()
        }

        binding.btnConsultAdd.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, ConsultingDetailFragment())
                .commit()
        }

        getMainKeywordsApi()
        getConversationListApi()

        return binding.root
    }

    private fun getMainKeywordsApi() {
        getKeywords.getKeywords().enqueue(object : retrofit2.Callback<KeywordsDto> {
            override fun onResponse(
                call: Call<KeywordsDto>, response: Response<KeywordsDto>
            ) {
                if (response.isSuccessful) {
                    val adapter = MainKeywordsAdapter()
                    binding.rvWhichTopic.adapter = adapter

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

    private fun getConversationListApi() {
        getConversationList.getConversationList().enqueue(object : retrofit2.Callback<ConversationListDto> {
            override fun onResponse(
                call: Call<ConversationListDto>, response: Response<ConversationListDto>
            ) {
                if (response.isSuccessful) {
                    val adapter = ConversationListAdapter()
                    binding.rvConversation.adapter = adapter

                    adapter.setOnItemClickListener(object : ConversationListAdapter.OnItemClickListener {
                        override fun onItemClick(conversations: Conversations) {
                            val recordingDetailFragment = RecordingDetailFragment()
                            val bundle = Bundle().apply {
                                putInt("conversationId", conversations.conversationId)
                                Log.d("conversationID", conversations.conversationId.toString())
                            }
                            recordingDetailFragment.arguments = bundle

                            parentFragmentManager.beginTransaction()
                                .replace(binding.fragmentContainer.id, recordingDetailFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    })

                    response.body()?.let {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        adapter.submitList(it.conversations.sortedByDescending {
                            dateFormat.parse(it.date)
                        })
                    }
                } else {
                    Log.d("error", "실패한 응답")
                }
            }
            override fun onFailure(call: Call<ConversationListDto>, t: Throwable) {
                t.message?.let { Log.d("error", it) } ?: "서버통신 실패(응답값 X)"
            }
        })
    }


}