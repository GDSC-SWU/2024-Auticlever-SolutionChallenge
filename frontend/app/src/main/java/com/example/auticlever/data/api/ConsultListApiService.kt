package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConsultListDto
import retrofit2.Call
import retrofit2.http.GET

interface ConsultListApiService {
    @GET("consultations")
    fun getConsultList(): Call<ConsultListDto>
}