package com.example.auticlever.data.api

import com.example.auticlever.data.dto.KeywordsDto
import retrofit2.Call
import retrofit2.http.GET

interface KeywordsApiService {
    @GET("topic-recommendation")
    fun getKeywords(): Call<KeywordsDto>
}