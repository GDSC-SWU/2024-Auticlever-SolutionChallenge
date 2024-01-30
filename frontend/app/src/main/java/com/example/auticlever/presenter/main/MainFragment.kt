package com.example.auticlever.presenter.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auticlever.R
import com.example.auticlever.databinding.FragmentMainBinding
import com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment
import com.example.auticlever.presenter.consultinglist.ConsultingListFragment
import com.example.auticlever.presenter.recording.ConsultationFragment
import com.example.auticlever.presenter.recording.RecordingFragment


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        return binding.root
    }

}