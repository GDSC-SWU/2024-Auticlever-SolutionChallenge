package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConversationData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ConversationDataApiService {
    @GET("conversation/{conversationId}/data")
    fun getConversationData(
        @Path("conversationId") conversationId: Int
    ): Call<ConversationData>
}