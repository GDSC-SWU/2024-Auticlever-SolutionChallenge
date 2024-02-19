package com.example.auticlever.data.api

import com.example.auticlever.data.dto.ConsultCsMemoResponseDto
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path


interface ConsultCsMemoApiService {
    @FormUrlEncoded
    @POST("/consultation/{consultationId}/csMemo")
    fun sendConsultCsMemoData(
        @Path("consultationId") consultationId: Int,
        @Field("title") title: String,
        @Field("csMemo") csMemo: String,
        @Field("mainMemo") mainMemo: String
    ): Call<ConsultCsMemoResponseDto>

}