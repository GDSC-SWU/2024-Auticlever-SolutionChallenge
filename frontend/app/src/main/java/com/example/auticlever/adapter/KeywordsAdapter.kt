package com.example.auticlever.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.R
import com.example.auticlever.databinding.ItemRecordingKeywordsBinding

class KeywordsAdapter :
    ListAdapter<String, KeywordsAdapter.KeywordsViewHolder>(KeywordsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recording_keywords, parent, false)
        return KeywordsViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeywordsViewHolder, position: Int) {
        if (currentList.isEmpty()) {
            // 데이터가 없는 경우, 빈 상태를 표시
            holder.showEmptyState()
        } else {
            // 데이터가 있는 경우, 실제 데이터를 표시
            holder.bind(getItem(position))
        }
    }

    class KeywordsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        //private lateinit var binding: ItemRecordingKeywordsBinding
        private var keywords = view.findViewById<TextView>(R.id.tv_recording_keywords)
        fun bind(recommended_keywords: String) {
            //binding.tvRecordingKeywords.text = recommended_keywords
            keywords.text = recommended_keywords
        }

        fun showEmptyState() {
            Log.d("error", "데이터 없음")
        }
    }
}

class KeywordsDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}