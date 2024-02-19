package com.example.auticlever.data.api

import com.example.auticlever.data.dto.DeleteConsultDetailDto
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface DeleteConsultDetailApiService {
    @DELETE("/consultation/{consultationId}")
    fun deleteConsultDetail(
        @Path("consultationId") consultationId: Int
    ): Call<DeleteConsultDetailDto>
}