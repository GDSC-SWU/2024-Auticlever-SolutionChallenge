package com.example.auticlever.presenter.recordloading

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.repeatOnLifecycle
import com.example.auticlever.R
import com.example.auticlever.databinding.FragmentRecordLoadingBinding
import com.example.auticlever.databinding.FragmentRecordingBinding
import com.example.auticlever.presenter.recording.RecordingFragment
import com.example.auticlever.presenter.recordingdetail.RecordingDetailFragment

class RecordLoadingFragment : Fragment() {

    lateinit var binding : FragmentRecordLoadingBinding
    lateinit var response : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordLoadingBinding.inflate(inflater)

        response = binding.tvResponse
        response.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, RecordingDetailFragment())
                .commit()
        }

        return binding.root
    }

}