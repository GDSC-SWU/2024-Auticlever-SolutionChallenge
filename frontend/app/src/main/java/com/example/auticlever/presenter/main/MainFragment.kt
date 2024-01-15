package com.example.auticlever.presenter.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auticlever.R
import com.example.auticlever.databinding.FragmentMainBinding
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

        return binding.root
    }

}