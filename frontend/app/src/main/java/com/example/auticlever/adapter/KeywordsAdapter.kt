package com.example.auticlever.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.databinding.ItemRecordingKeywordsBinding

class KeywordsAdapter :
    ListAdapter<String, KeywordsAdapter.KeywordsViewHolder>(KeywordsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordsViewHolder {
        val binding = ItemRecordingKeywordsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KeywordsViewHolder(binding)
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

    class KeywordsViewHolder(private val binding: ItemRecordingKeywordsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recommendedKeywords: String) {
            binding.tvRecordingKeywords.text = recommendedKeywords
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
