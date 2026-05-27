package com.example.data.Finance.datasource.remote.api

import com.example.data.Finance.datasource.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface FinanceApiService {

    @GET("api/finance/sources")
    suspend fun listSources(): List<SourceDto>

    @POST("api/finance/sources")
    suspend fun createSource(@Body req: CreateSourceRequestDto): SourceDto

    @PUT("api/finance/sources/{id}")
    suspend fun updateSource(@Path("id") id: Int, @Body req: UpdateSourceRequestDto): SourceDto

    @DELETE("api/finance/sources/{id}")
    suspend fun deleteSource(@Path("id") id: Int): Response<Unit>

    @GET("api/finance/tags")
    suspend fun listTags(): List<TagDto>

    @POST("api/finance/tags")
    suspend fun createTag(@Body req: CreateTagRequestDto): TagDto

    @PUT("api/finance/tags/{id}")
    suspend fun updateTag(@Path("id") id: Int, @Body req: UpdateTagRequestDto): TagDto

    @DELETE("api/finance/tags/{id}")
    suspend fun deleteTag(@Path("id") id: Int): Response<Unit>

    @GET("api/finance/tags/{id}/transactions")
    suspend fun listTagTransactions(@Path("id") id: Int): List<TransactionDto>

    @PUT("api/finance/tags/{id}/transactions")
    suspend fun assignTagToTransactions(
        @Path("id") id: Int,
        @Body req: AssignTagRequestDto,
    ): Response<Unit>

    @GET("api/finance/transactions")
    suspend fun listTransactions(
        @Query("type") type: String? = null,
        @Query("from") from: String? = null,
        @Query("to")   to:   String? = null,
    ): List<TransactionDto>

    @POST("api/finance/transactions")
    suspend fun createTransaction(@Body req: CreateTransactionRequestDto): TransactionDto

    @PUT("api/finance/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Int,
        @Body req: UpdateTransactionRequestDto,
    ): TransactionDto

    @DELETE("api/finance/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int): Response<Unit>

    @GET("api/finance/goals")
    suspend fun listGoals(): List<GoalDto>

    @GET("api/finance/goals/{id}")
    suspend fun getGoal(@Path("id") id: Int): GoalDto

    @POST("api/finance/goals")
    suspend fun createGoal(@Body req: CreateGoalRequestDto): GoalDto

    @PUT("api/finance/goals/{id}")
    suspend fun updateGoal(@Path("id") id: Int, @Body req: UpdateGoalRequestDto): GoalDto

    @DELETE("api/finance/goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Int): Response<Unit>

    @GET("api/finance/analytics")
    suspend fun getAnalytics(
        @Query("month") month: String,
        @Query("type") type: String,
    ): AnalyticsResponseDto

    @Multipart
    @POST("api/finance/reports/import")
    suspend fun importBankReport(
        @Query("sourceId") sourceId: Int,
        @Part file: MultipartBody.Part,
    ): ImportReportDto
}
