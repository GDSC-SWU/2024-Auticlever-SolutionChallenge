package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultCsMemoResponseDto(
    @SerialName("csMemo_message")
    val csMemoMessage: String,
    @SerialName("mainMemo_message")
    val mainMemoMessage: String
)