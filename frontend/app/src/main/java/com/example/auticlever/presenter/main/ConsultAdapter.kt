package com.example.auticlever.presenter.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.R

class ConsultAdapter :
    ListAdapter<Consult, ConsultAdapter.ConsultViewHolder>(ConsultDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main_consult, parent, false)
        return ConsultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultViewHolder, position: Int) {
        holder.bind(getItem(position)) //position에 해당하는 data type을 전달
    }

    class ConsultViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val consultHistory=view.findViewById<TextView>(R.id.tv_many_emotion)
        fun bind(consult : Consult) {
            consultHistory.text = consult.consult
        }
    }
}

class ConsultDiffCallback : DiffUtil.ItemCallback<Consult>() {
    override fun areItemsTheSame(oldItem: Consult, newItem: Consult): Boolean {
        return oldItem.consult == newItem.consult
    }

    override fun areContentsTheSame(oldItem: Consult, newItem: Consult): Boolean {
        return oldItem == newItem
    }
}