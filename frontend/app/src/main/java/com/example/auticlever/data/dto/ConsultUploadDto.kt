package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultUploadDto(
    @SerialName("message")
    val message: String,

    @SerialName("summary")
    val summary: String
)