package com.example.auticlever.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
object ConsultCsMemoApiFactory {
    private const val BASE_URL = "http://34.47.76.246:8000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/x-www-form-urlencoded".toMediaType()))
            .build()
    }

    val consultCsMemoApiService: ConsultCsMemoApiService by lazy {
        retrofit.create(ConsultCsMemoApiService::class.java)
    }
}