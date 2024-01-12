package com.example.auticlever.presenter.main

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.auticlever.R

class ConsultViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val consultHistory=view.findViewById<TextView>(R.id.tv_many_emotion)
    fun bind(consult : Consult) {
        consultHistory.text = consult.consult
    }
}