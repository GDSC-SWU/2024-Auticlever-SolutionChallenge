package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConversationMemoDto
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationMemoApiService {
    @FormUrlEncoded
    @POST("/conversation/{conversationId}/cvMemo")
    fun postConversationMemo(
        @Path("conversationId") conversationId: Int,
        @Field("content") memo: String,
    ): Call<ConversationMemoDto>
}