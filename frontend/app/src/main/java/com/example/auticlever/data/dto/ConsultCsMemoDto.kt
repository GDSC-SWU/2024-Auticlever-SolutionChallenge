package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultCsMemoRequestDto(
    @SerialName("title")
    val title: String,
    @SerialName("csMemo")
    val csMemo: String,
    @SerialName("mainMemo")
    val mainMemo: String
)

@Serializable
data class ConsultCsMemoResponseDto(
    @SerialName("csMemo_message")
    val csMemoMessage: String,
    @SerialName("mainMemo_message")
    val mainMemoMessage: String
)