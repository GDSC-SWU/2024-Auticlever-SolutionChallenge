package com.example.auticlever.data

import com.example.auticlever.data.api.ConsultListApiService
import com.example.auticlever.data.api.ConversationDataApiService
import com.example.auticlever.data.api.ConversationFileApiService
import com.example.auticlever.data.api.ConversationListApiService
import com.example.auticlever.data.api.KeywordsApiService
import com.google.android.gms.measurement.sdk.R
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ApiPool {
    val getConversationData = RetrofitPool.retrofit.create(ConversationDataApiService::class.java)
    val getConversationFile = RetrofitPool.retrofit.create(ConversationFileApiService::class.java)
    val getConversationList = RetrofitPool.retrofit.create(ConversationListApiService::class.java)
    val getKeywords = RetrofitPool.retrofit.create(KeywordsApiService::class.java)
    val getConsultList = RetrofitPool.retrofit.create(ConsultListApiService::class.java)
}

object RetrofitPool {
    private const val BASE_URL =
        "http://34.47.76.246:8000"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
