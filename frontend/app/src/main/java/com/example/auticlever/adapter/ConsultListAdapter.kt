package com.example.auticlever.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.data.dto.Consultations
import com.example.auticlever.databinding.ItemConsultListDayBinding

class ConsultListAdapter() :
    ListAdapter<Consultations, ConsultListAdapter.ConsultListViewHolder>(ConsultListDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(consultations: Consultations)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultListViewHolder {
        val binding = ItemConsultListDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConsultListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConsultListViewHolder, position: Int) {
        if (currentList.isEmpty()) {
            // 데이터가 없는 경우, 빈 상태를 표시
            holder.showEmptyState()
        } else {
            // 데이터가 있는 경우, 실제 데이터를 표시
            holder.bind(getItem(position))
        }
    }

    inner class ConsultListViewHolder(private val binding: ItemConsultListDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(consultations: Consultations) {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val consultation = getItem(position)
                    itemClickListener?.onItemClick(consultation)
                }
            }

            val month = getMonth(consultations.date.substring(5, 7))
            val day = consultations.date.substring(8, 10)

            binding.tvDay1.text = month
            binding.tvDay2.text = day
            if(consultations.title==null){
                binding.tvTitle.text ="Blank Title"
            }else{
                binding.tvTitle.text=consultations.title
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

class ConsultListDiffCallback : DiffUtil.ItemCallback<Consultations>() {
    override fun areItemsTheSame(oldItem: Consultations, newItem: Consultations): Boolean {
        return oldItem.consultationId == newItem.consultationId
    }

    override fun areContentsTheSame(oldItem: Consultations, newItem: Consultations): Boolean {
        return oldItem == newItem
    }
}