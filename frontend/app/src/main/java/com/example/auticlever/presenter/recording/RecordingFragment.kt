package com.example.auticlever.presenter.recording

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.auticlever.adapter.RecordingPagerAdapter
import com.example.auticlever.databinding.FragmentRecordingBinding
import com.example.auticlever.presenter.main.MainFragment
import com.example.auticlever.presenter.recordloading.RecordLoadingFragment

class RecordingFragment : Fragment() {

    lateinit var binding : FragmentRecordingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordingBinding.inflate(inflater)

        setUpViewPager()

        binding.tvDelete.setOnClickListener{
            DeleteDialog()
        }
        binding.ibRecordingArrow.setOnClickListener{
            DeleteDialog()
        }
        binding.tvSave.setOnClickListener{
            SaveDialog()
        }

        return binding.root
    }

    fun fragmentdelete() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()
    }

    fun fragmentsave() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, RecordLoadingFragment())
            .commit()
    }

    private fun setUpViewPager() {

        var adapter = RecordingPagerAdapter(childFragmentManager)
        adapter.addFragment(KeywordsFragment(), "keyword")
        adapter.addFragment(ConsultationFragment(), "consultation")

        binding.viewPager!!.adapter = adapter
        binding.tabLayout!!.setupWithViewPager(binding.viewPager)
    }

    private fun DeleteDialog() {
        val DeleteDialog = DeleteDialog(requireContext(), this)
        DeleteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        DeleteDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        DeleteDialog.show()
    }

    private fun SaveDialog() {
        val SaveDialog = SaveDialog(requireContext(), this)
        SaveDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        SaveDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        SaveDialog.show()
    }
}