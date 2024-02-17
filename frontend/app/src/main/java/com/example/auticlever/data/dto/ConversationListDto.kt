package com.example.auticlever.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationListDto(
    @SerialName("conversations")
    val conversations: List<Conversations>
)

@Serializable
data class Conversations(
    @SerialName("conversationId")
    val conversationId: Int,
    @SerialName("keyword")
    val keyword: String,
    @SerialName("cvMemo")
    val cvMemo: List<ConversationMemo>,
    @SerialName("date")
    val date: String
)

@Serializable
data class ConversationMemo(
    @SerialName("memoId")
    val memoId: Int,
    @SerialName("content")
    val content: String
)
