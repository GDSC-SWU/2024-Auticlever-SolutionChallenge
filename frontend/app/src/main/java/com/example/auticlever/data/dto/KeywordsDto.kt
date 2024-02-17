package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordsDto(
    @SerialName("recommended_keywords")
    val recommended_keywords: List<String>
)
