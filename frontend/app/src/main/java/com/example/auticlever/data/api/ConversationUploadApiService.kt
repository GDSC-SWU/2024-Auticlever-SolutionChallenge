package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConversationUploadDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ConversationUploadApiService {
    @Multipart
    @POST("/conversation")
    fun getConversationUpload(
        @Part file: MultipartBody.Part
    ) : Call<ConversationUploadDto>
}