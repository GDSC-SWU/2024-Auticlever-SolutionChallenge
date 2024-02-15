package com.example.auticlever

import android.R
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.auticlever.databinding.ActivityMainBinding
import com.example.auticlever.databinding.FragmentConsultingDetailBinding
import com.example.auticlever.presenter.consultingdetail.ConsultingDetailFragment
import com.example.auticlever.presenter.consultinglist.ConsultingListFragment
import com.example.auticlever.presenter.main.MainFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        val mainFragment = MainFragment()
//        supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout, mainFragment).commit()
        if (savedInstanceState == null) {
            // 액티비티가 처음 생성될 때 프래그먼트를 추가
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainer.id, MainFragment())
                .commit()
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

}