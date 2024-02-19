package com.example.auticlever.data.api

import com.example.auticlever.data.dto.MainMemoDto
import retrofit2.Call
import retrofit2.http.GET

interface MainMemoApiService {
    @GET("main-memo")
    fun getMainMemo(): Call<MainMemoDto>
}