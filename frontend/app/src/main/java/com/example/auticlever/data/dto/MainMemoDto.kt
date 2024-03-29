package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainMemoDto(
    @SerialName("content")
    val content: String?
)
