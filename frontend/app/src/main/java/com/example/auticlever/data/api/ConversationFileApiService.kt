package com.example.auticlever.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ConversationFileApiService {
    @GET("conversation/{conversationId}/file")
    @Streaming
    fun getConversationFile(
        @Path("conversationId") conversationId: Int
    ): Call<ResponseBody>
}