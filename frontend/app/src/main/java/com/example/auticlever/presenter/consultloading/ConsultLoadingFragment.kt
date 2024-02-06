package com.example.auticlever.presenter.consultloading

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.auticlever.databinding.FragmentRecordLoadingBinding

class ConsultLoadingFragment : Fragment() {

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
            ExitDialog()
        }

        return binding.root
    }

    private fun ExitDialog() {
        val ExitDialog = ExitDialog(requireContext(), this)
        ExitDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ExitDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        ExitDialog.show()
    }

}