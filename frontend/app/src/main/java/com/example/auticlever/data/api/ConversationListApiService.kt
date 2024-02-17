package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConversationListDto
import retrofit2.Call
import retrofit2.http.GET

interface ConversationListApiService {
    @GET("conversations")
    fun getConversationList(): Call<ConversationListDto>
}