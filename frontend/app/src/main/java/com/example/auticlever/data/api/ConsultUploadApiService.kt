package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConsultUploadDto
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ConsultUploadApiService {
    @Multipart
    @POST("consultation")
    fun uploadConsultation(
        @Part
        file: MultipartBody.Part
    ): Call<ConsultUploadDto>
}