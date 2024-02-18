package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ConsultListDto(
    @SerialName("consultations")
    val consultations: List<Consultations>
)

@Serializable
data class Consultations(
    @SerialName("consultationId")
    val consultationId: Int,

    @SerialName("title")
    val title: String,

    @SerialName("date")
    val date: String,

    @SerialName("csMemo")
    val csMemo: String
)
