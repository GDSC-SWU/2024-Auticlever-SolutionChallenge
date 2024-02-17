package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationData(
    @SerialName("conversation_data")
    val conversation_data: ConversationDetail,
    @SerialName("cvMemo_data")
    val cvMemo_data: List<CvMemoData>
)

@Serializable
data class ConversationDetail(
    @SerialName("cvId")
    val cvId: Int,
    @SerialName("filePath")
    val filePath: String,
    @SerialName("content")
    val content: String,
    @SerialName("keyword")
    val keyword: String,
    @SerialName("summary")
    val summary: String,
    @SerialName("date")
    val date: String
)

@Serializable
data class CvMemoData(
    @SerialName("memoId")
    val memoId: Int,
    @SerialName("conversationId")
    val conversationId: Int,
    @SerialName("content")
    val content: String
)

