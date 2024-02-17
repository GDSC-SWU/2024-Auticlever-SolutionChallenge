package com.example.auticlever.data

import com.example.auticlever.data.api.ConsultUploadApiFactory
import com.example.auticlever.data.api.ConsultUploadApiService
import com.example.auticlever.data.api.ConversationDataApiService
import com.example.auticlever.data.api.ConversationFileApiService
import com.example.auticlever.data.api.ConversationListApiService
import com.example.auticlever.data.api.ConversationUploadApiService
import com.example.auticlever.data.api.KeywordsApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiPool {
    val getConversationData = RetrofitPool.retrofit.create(ConversationDataApiService::class.java)
    val getConversationFile = RetrofitPool.retrofit.create(ConversationFileApiService::class.java)
    val getConversationList = RetrofitPool.retrofit.create(ConversationListApiService::class.java)
    val getKeywords = RetrofitPool.retrofit.create(KeywordsApiService::class.java)
    val getConversationUpload = RetrofitPool.retrofit.create(ConversationUploadApiService::class.java)
}

object RetrofitPool {

    private const val BASE_URL =
        "http://34.47.76.246:8000"

    val client = OkHttpClient.Builder()
        .connectTimeout(1000, TimeUnit.SECONDS)
        .readTimeout(1000, TimeUnit.SECONDS)
        .writeTimeout(1000, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

}
