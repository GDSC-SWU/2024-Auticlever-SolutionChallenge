package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConsultDataDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ConsultDataApiService {
    @GET("/consultation/{consultationId}")
    fun getConsultData(
        @Path("consultationId") consultationId: Int
    ): Call<ConsultDataDto>
}