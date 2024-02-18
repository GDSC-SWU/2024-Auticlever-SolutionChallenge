package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConsultCsMemoRequestDto
import com.example.auticlever.data.dto.ConsultCsMemoResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ConsultCsMemoApiService {
    @POST("consultation/{consultationId}/csMemo")
    fun sendConsultCsMemoData(
        @Path("consultationId") consultationId: Int,
        @Body requestDto: ConsultCsMemoRequestDto
    ): Call<ConsultCsMemoResponseDto>

    @GET("consultation/{consultationId}")
    fun getConsultationDetails(
        @Path("consultationId") consultationId: Int
    ): Call<ConsultCsMemoRequestDto>

    @PUT("consultation/{consultationId}/csMemo")
    fun updateConsultationDetails(
        @Path("consultationId") consultationId: Int,
        @Body details: ConsultCsMemoRequestDto
    ): Call<ConsultCsMemoResponseDto>
}