package com.example.domain.Finance.repository

import com.example.domain.Finance.model.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

interface FinanceRepository {

    fun observeDataVersion(): Flow<Long>
    fun observeOfflineMode(): Flow<Boolean>

    fun observeAnalyticsTarget(): Flow<YearMonth?>
    suspend fun consumeAnalyticsTarget()

    suspend fun syncNow()

    suspend fun getSources(): List<Source>
    suspend fun createSource(name: String, type: SourceType): Source
    suspend fun updateSource(id: Int, name: String, type: SourceType): Source
    suspend fun deleteSource(id: Int)

    suspend fun getTags(): List<Tag>
    suspend fun createTag(name: String): Tag
    suspend fun updateTag(id: Int, name: String): Tag
    suspend fun deleteTag(id: Int)

    suspend fun getTransactions(
        type: TransactionType? = null,
        from: LocalDate? = null,
        to: LocalDate? = null,
    ): List<Transaction>

    suspend fun getTransactionsByTag(tagId: Int): List<Transaction>

    suspend fun createTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction

    suspend fun updateTransaction(
        id: Int,
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction

    suspend fun deleteTransaction(id: Int)

    suspend fun assignTagToTransactions(tagId: Int, transactionIds: List<Int>)

    suspend fun getGoals(): List<Goal>
    suspend fun getGoal(id: Int): Goal
    suspend fun createGoal(
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal

    suspend fun updateGoal(
        id: Int,
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal

    suspend fun deleteGoal(id: Int)

    suspend fun getAnalytics(month: YearMonth, type: TransactionType): Analytics

    suspend fun importBankReport(fileName: String, sourceId: Int, content: ByteArray): ImportReport
}
