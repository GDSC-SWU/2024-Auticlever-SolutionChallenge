package com.example.auticlever.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.databinding.ItemMainKeywordBinding

class MainKeywordsAdapter :
    ListAdapter<String, MainKeywordsAdapter.MainKeywordsViewHolder>(MainKeywordsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainKeywordsViewHolder {
        val binding = ItemMainKeywordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainKeywordsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainKeywordsViewHolder, position: Int) {
        if (currentList.isEmpty()) {
            // 데이터가 없는 경우, 빈 상태를 표시
            holder.showEmptyState()
        } else {
            // 데이터가 있는 경우, 실제 데이터를 표시
            holder.bind(getItem(position))
        }
    }

    class MainKeywordsViewHolder(private val binding: ItemMainKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recommendedKeywords: String) {
            binding.tvKeyword.text = recommendedKeywords
        }

        fun showEmptyState() {
            Log.d("error", "데이터 없음")
        }
    }
}

class MainKeywordsDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
