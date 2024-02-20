package com.example.auticlever.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.data.dto.Consultations
import com.example.auticlever.databinding.ItemRecordingConverBinding

class RecordingConsultListAdapter() :
    ListAdapter<Consultations, RecordingConsultListAdapter.RecordingConsultListViewHolder>(RecordingConsultListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingConsultListViewHolder {
        val binding = ItemRecordingConverBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordingConsultListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordingConsultListViewHolder, position: Int) {
        if (currentList.isEmpty()) {
            // 데이터가 없는 경우, 빈 상태를 표시
            holder.showEmptyState()
        } else {
            // 데이터가 있는 경우, 실제 데이터를 표시
            holder.bind(getItem(position))
        }
    }

    inner class RecordingConsultListViewHolder(private val binding: ItemRecordingConverBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(consultations: Consultations) {
            val month = getMonth(consultations.date.substring(5, 7))
            val day = consultations.date.substring(8, 10)

            binding.tvDay1.text = month
            binding.tvDay2.text = day
            if(consultations.title==null){
                binding.tvText1.text ="Blank Title"
            }else{
                binding.tvText1.text=consultations.title
            }

            if(consultations.title==null){
                binding.tvMemo.text ="Blank Memo"
            }else{
                binding.tvMemo.text =consultations.csMemo
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

class RecordingConsultListDiffCallback : DiffUtil.ItemCallback<Consultations>() {
    override fun areItemsTheSame(oldItem: Consultations, newItem: Consultations): Boolean {
        return oldItem.consultationId == newItem.consultationId
    }

    override fun areContentsTheSame(oldItem: Consultations, newItem: Consultations): Boolean {
        return oldItem == newItem
    }
}