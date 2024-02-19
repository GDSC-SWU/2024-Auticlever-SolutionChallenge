package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationUploadDto(
    @SerialName("message")
    val message: String,
    @SerialName("keywords")
    val keywords: List<String>,
    @SerialName("summary")
    val summary: String
)
