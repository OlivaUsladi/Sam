package com.example.data.Recipes.datasource.remote

import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Auth.datasource.remote.api.AuthApiService
import com.example.data.Auth.security.AuthInterceptor
import com.example.data.Auth.security.TokenAuthenticator
import com.example.data.Finance.datasource.remote.api.FinanceApiService
import com.example.data.Hints.datasource.remote.api.ArticleApiService
import com.example.data.Recipes.datasource.remote.api.RecipeApiService
import com.example.data.Recipes.datasource.remote.api.ShoppingListApiService
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //Для эмулятора Android — 10.0.2.2:8080,
    //для реального устройства — IP компьютера в локальной сети.

    private const val BASE_URL = "http://192.168.0.102:8080/"

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit

    lateinit var recipeApiService: RecipeApiService
        private set
    lateinit var shoppingListApiService: ShoppingListApiService
        private set
    lateinit var articleApiService: ArticleApiService
        private set

    lateinit var financeApiService: FinanceApiService
        private set
    lateinit var authApiService: AuthApiService
        private set

    fun init(tokenStorage: TokenStorage) {
        val baseLogging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, 1, TimeUnit.MILLISECONDS))
            .retryOnConnectionFailure(true)
            .addInterceptor(baseLogging)
            .build()

        val authRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        authApiService = authRetrofit.create(AuthApiService::class.java)

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(0, 1, TimeUnit.MILLISECONDS))
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthInterceptor(tokenStorage))
            .authenticator(TokenAuthenticator(tokenStorage) { authApiService })
            .addInterceptor(baseLogging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        recipeApiService = retrofit.create(RecipeApiService::class.java)
        shoppingListApiService = retrofit.create(ShoppingListApiService::class.java)
        articleApiService = retrofit.create(ArticleApiService::class.java)
        financeApiService = retrofit.create(FinanceApiService::class.java)

    }
}
