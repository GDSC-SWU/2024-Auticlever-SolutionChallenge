package com.example.auticlever.presenter.consultinglist

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.replace
import com.example.auticlever.MainActivity
import com.example.auticlever.R
import com.example.auticlever.databinding.ActivityMainBinding
import com.example.auticlever.databinding.FragmentConsultingDetailBinding
import com.example.auticlever.databinding.FragmentConsultingListBinding
import com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment
import com.example.auticlever.presenter.main.MainFragment


class ConsultingListFragment : Fragment() {
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
        return binding.root

    }

}
