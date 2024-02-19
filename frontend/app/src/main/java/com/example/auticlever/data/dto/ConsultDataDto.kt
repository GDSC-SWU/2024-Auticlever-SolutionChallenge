package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class ConsultDataDto(

    @SerialName("consultation_data")
    val consultation_data: ConsultationDetail,
    @SerialName("csMemo_data")
    val csMemo_data: List<CsMemoData>
)

@Serializable
data class ConsultationDetail(
    @SerialName("csId")
    val csId : Int,
    @SerialName("summary")
    val summary : String,
    @SerialName("date")
    val date : String,
    @SerialName("content")
    val content : String,
)

@Serializable
data class CsMemoData(
    @SerialName("memoId")
    val memoId: Int,
    @SerialName("consultationId")
    val consultationId: Int,
    @SerialName("content")
    val content: String,
    @SerialName("title")
    val title: String?
)
