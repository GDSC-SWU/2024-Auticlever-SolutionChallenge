package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConversationDeleteDto
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ConversationDeleteApiService {
    @DELETE("/conversation/{conversationId}")
    fun deleteConversation(
        @Path("conversationId") conversationId: Int
    ): Call<ConversationDeleteDto>
}