package com.example.auticlever.adapter

import com.example.auticlever.databinding.ItemMainConverBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.data.dto.Conversations

class ConversationListAdapter() :
    ListAdapter<Conversations, ConversationListAdapter.ConversationListViewHolder>(ConversationListDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(conversations: Conversations)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationListViewHolder {
        val binding = ItemMainConverBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationListViewHolder, position: Int) {
        if (currentList.isEmpty()) {
            // 데이터가 없는 경우, 빈 상태를 표시
            holder.showEmptyState()
        } else {
            // 데이터가 있는 경우, 실제 데이터를 표시
            holder.bind(getItem(position))
        }
    }

    inner class ConversationListViewHolder(private val binding: ItemMainConverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(conversations: Conversations) {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val conversation = getItem(position)
                    itemClickListener?.onItemClick(conversation)
                }
            }

            val month = getMonth(conversations.date.substring(5, 7))
            val day = conversations.date.substring(8, 10)
            binding.tvDay1.text = month
            binding.tvDay2.text = day
            binding.tvText1.text = conversations.keyword
            if (conversations.cvMemo.firstOrNull()?.content != null) {
                binding.tvMemo.text = conversations.cvMemo.firstOrNull()?.content
            }
            else {
                binding.tvMemo.visibility = View.GONE
            }

        }

        fun showEmptyState() {
            Log.d("error", "데이터 없음")
        }

        private fun getMonth(month: String): String {
            return when (month) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> throw IllegalArgumentException("Invalid month: $month")
            }
        }
    }
}

class ConversationListDiffCallback : DiffUtil.ItemCallback<Conversations>() {
    override fun areItemsTheSame(oldItem: Conversations, newItem: Conversations): Boolean {
        return oldItem.conversationId == newItem.conversationId
    }

    override fun areContentsTheSame(oldItem: Conversations, newItem: Conversations): Boolean {
        return oldItem == newItem
    }
}