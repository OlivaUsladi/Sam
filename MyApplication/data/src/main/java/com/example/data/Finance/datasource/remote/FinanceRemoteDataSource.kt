package com.example.data.Finance.datasource.remote

import com.example.data.Finance.datasource.remote.api.FinanceApiService
import com.example.data.Finance.datasource.remote.dto.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class FinanceRemoteDataSource(private val api: FinanceApiService) {

    suspend fun listSources(): List<SourceDto> = api.listSources()
    suspend fun createSource(name: String, type: String): SourceDto =
        api.createSource(CreateSourceRequestDto(name, type))
    suspend fun updateSource(id: Int, name: String, type: String): SourceDto =
        api.updateSource(id, UpdateSourceRequestDto(name, type))
    suspend fun deleteSource(id: Int) {
        api.deleteSource(id)
    }

    suspend fun listTags(): List<TagDto> = api.listTags()
    suspend fun createTag(name: String): TagDto = api.createTag(CreateTagRequestDto(name))
    suspend fun updateTag(id: Int, name: String): TagDto =
        api.updateTag(id, UpdateTagRequestDto(name))
    suspend fun deleteTag(id: Int) { api.deleteTag(id) }
    suspend fun listTagTransactions(id: Int): List<TransactionDto> = api.listTagTransactions(id)
    suspend fun assignTagToTransactions(id: Int, transactionIds: List<Int>) {
        api.assignTagToTransactions(id, AssignTagRequestDto(transactionIds))
    }

    suspend fun listTransactions(type: String?, from: String?, to: String?): List<TransactionDto> =
        api.listTransactions(type, from, to)

    suspend fun createTransaction(req: CreateTransactionRequestDto): TransactionDto =
        api.createTransaction(req)

    suspend fun updateTransaction(id: Int, req: UpdateTransactionRequestDto): TransactionDto =
        api.updateTransaction(id, req)

    suspend fun deleteTransaction(id: Int) { api.deleteTransaction(id) }

    suspend fun listGoals(): List<GoalDto> = api.listGoals()
    suspend fun getGoal(id: Int): GoalDto = api.getGoal(id)
    suspend fun createGoal(req: CreateGoalRequestDto): GoalDto = api.createGoal(req)
    suspend fun updateGoal(id: Int, req: UpdateGoalRequestDto): GoalDto = api.updateGoal(id, req)
    suspend fun deleteGoal(id: Int) { api.deleteGoal(id) }

    suspend fun getAnalytics(month: String, type: String): AnalyticsResponseDto =
        api.getAnalytics(month, type)


    suspend fun importBankReport(fileName: String, sourceId: Int, content: ByteArray): ImportReportDto {
        val body = content.toRequestBody("application/pdf".toMediaType())
        val part = MultipartBody.Part.createFormData("file", fileName, body)
        return api.importBankReport(sourceId, part)
    }
}
