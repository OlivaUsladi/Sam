package com.example.data.Finance.repository

import com.example.data.Finance.datasource.remote.FinanceRemoteDataSource
import com.example.data.Finance.datasource.remote.dto.*
import com.example.data.Finance.datasource.remote.mapper.FinanceMappers.toDomain
import com.example.domain.Finance.model.*
import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

class FinanceRepositoryImpl(
    private val remote: FinanceRemoteDataSource
) : FinanceRepository {

    private val isoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val ymFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val _dataVersion = MutableStateFlow(0L)
    private val versionCounter = AtomicLong(0L)
    private fun bump() { _dataVersion.value = versionCounter.incrementAndGet() }

    override fun observeDataVersion(): Flow<Long> = _dataVersion.asStateFlow()

    override suspend fun getSources(): List<Source> =
        remote.listSources().map { it.toDomain() }

    override suspend fun createSource(name: String, type: SourceType): Source =
        remote.createSource(name, type.raw).toDomain().also { bump() }

    override suspend fun updateSource(id: Int, name: String, type: SourceType): Source =
        remote.updateSource(id, name, type.raw).toDomain().also { bump() }

    override suspend fun deleteSource(id: Int) {
        remote.deleteSource(id); bump()
    }

    override suspend fun getTags(): List<Tag> = remote.listTags().map { it.toDomain() }
    override suspend fun createTag(name: String): Tag =
        remote.createTag(name).toDomain().also { bump() }
    override suspend fun updateTag(id: Int, name: String): Tag =
        remote.updateTag(id, name).toDomain().also { bump() }

    override suspend fun deleteTag(id: Int) {
        remote.deleteTag(id); bump()
    }

    override suspend fun getTransactions(
        type: TransactionType?,
        from: LocalDate?,
        to: LocalDate?
    ): List<Transaction> = remote.listTransactions(
        type?.raw,
        from?.format(isoDate),
        to?.format(isoDate),
    ).map { it.toDomain() }

    override suspend fun getTransactionsByTag(tagId: Int): List<Transaction> =
        remote.listTagTransactions(tagId).map { it.toDomain() }

    override suspend fun createTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction = remote.createTransaction(
        CreateTransactionRequestDto(
            name = name,
            amount = amount,
            type = type.raw,
            description = description,
            transactionDate = date.format(isoDate),
            sourceId = sourceId,
            tagId = tagId,
        )
    ).toDomain().also { bump() }

    override suspend fun updateTransaction(
        id: Int,
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction = remote.updateTransaction(
        id,
        UpdateTransactionRequestDto(
            name = name,
            amount = amount,
            type = type.raw,
            description = description,
            transactionDate = date.format(isoDate),
            sourceId = sourceId,
            tagId = tagId,
        )
    ).toDomain().also { bump() }

    override suspend fun deleteTransaction(id: Int) {
        remote.deleteTransaction(id); bump()
    }

    override suspend fun assignTagToTransactions(tagId: Int, transactionIds: List<Int>) {
        remote.assignTagToTransactions(tagId, transactionIds); bump()
    }

    override suspend fun getGoals(): List<Goal> = remote.listGoals().map { it.toDomain() }

    override suspend fun getGoal(id: Int): Goal = remote.getGoal(id).toDomain()

    override suspend fun createGoal(
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal = remote.createGoal(
        CreateGoalRequestDto(
            name = name,
            description = description,
            targetAmount = targetAmount,
            targetDate = targetDate?.format(isoDate),
            monthlyAmount = monthlyAmount,
        )
    ).toDomain().also { bump() }

    override suspend fun updateGoal(
        id: Int,
        name: String,
        description: String?,
        targetAmount: BigDecimal,
        currentAmount: BigDecimal,
        targetDate: LocalDate?,
        monthlyAmount: BigDecimal?,
    ): Goal = remote.updateGoal(
        id,
        UpdateGoalRequestDto(
            name = name,
            description = description,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            targetDate = targetDate?.format(isoDate),
            monthlyAmount = monthlyAmount,
        )
    ).toDomain().also { bump() }

    override suspend fun deleteGoal(id: Int) {
        remote.deleteGoal(id); bump()
    }

    override suspend fun getAnalytics(month: YearMonth, type: TransactionType): Analytics =
        remote.getAnalytics(month.format(ymFormat), type.raw).toDomain()

    override suspend fun importBankReport(
        fileName: String,
        sourceId: Int,
        content: ByteArray,
    ): ImportReport = remote.importBankReport(fileName, sourceId, content)
        .toDomain()
        .also { bump() }
}
