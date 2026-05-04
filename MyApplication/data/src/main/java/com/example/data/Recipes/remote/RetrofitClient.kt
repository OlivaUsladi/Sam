package com.example.data.Recipes.remote

import com.example.data.Recipes.remote.api.RecipeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Для эмулятора Android сюда вставляем 10.0.2.2
    // Для реального устройства — IP компьютера (192.168.0.102)
    //IP компьютера кстати Сеть и Интрнет -> Свойста -> IPv4
    private const val BASE_URL = "http://192.168.0.102:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("userId", "1")  //ВОТ ЭТО ПОКА НЕТ АВТОРИЗАЦИИ
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val recipeApiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)
}