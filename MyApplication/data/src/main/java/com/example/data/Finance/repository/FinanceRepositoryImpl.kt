package com.example.data.Finance.repository

import com.example.data.Finance.datasource.local.FinanceLocalDataSource
import com.example.domain.Finance.model.*
import com.example.domain.Finance.repository.FinanceRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

class FinanceRepositoryImpl(
    private val local: FinanceLocalDataSource
) : FinanceRepository {

    override suspend fun getSources(): List<Source> = local.listSources()
    override suspend fun createSource(name: String, type: SourceType): Source =
        local.createSource(name, type)
    override suspend fun updateSource(id: Int, name: String, type: SourceType): Source =
        local.updateSource(id, name, type)
    override suspend fun deleteSource(id: Int) = local.deleteSource(id)

    override suspend fun getTags(): List<Tag> = local.listTags()
    override suspend fun createTag(name: String): Tag = local.createTag(name)
    override suspend fun updateTag(id: Int, name: String): Tag = local.updateTag(id, name)
    override suspend fun deleteTag(id: Int) = local.deleteTag(id)

    override suspend fun getTransactions(
        type: TransactionType?, from: LocalDate?, to: LocalDate?,
    ): List<Transaction> = local.listTransactions(type, from, to)

    override suspend fun getTransactionsByTag(tagId: Int): List<Transaction> =
        local.listTransactionsByTag(tagId)

    override suspend fun createTransaction(
        name: String, amount: BigDecimal, type: TransactionType,
        description: String?, date: LocalDate, sourceId: Int, tagId: Int?,
    ): Transaction = local.createTransaction(
        name, amount, type, description, date, sourceId, tagId)

    override suspend fun updateTransaction(
        id: Int, name: String, amount: BigDecimal, type: TransactionType,
        description: String?, date: LocalDate, sourceId: Int, tagId: Int?,
    ): Transaction = local.updateTransaction(
        id, name, amount, type, description, date, sourceId, tagId)

    override suspend fun deleteTransaction(id: Int) = local.deleteTransaction(id)

    override suspend fun assignTagToTransactions(tagId: Int, transactionIds: List<Int>) =
        local.assignTagToTransactions(tagId, transactionIds)

    override suspend fun getGoals(): List<Goal> = local.listGoals()
    override suspend fun getGoal(id: Int): Goal = local.getGoal(id)
    override suspend fun createGoal(
        name: String, description: String?, targetAmount: BigDecimal,
        targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal = local.createGoal(name, description, targetAmount, targetDate, monthlyAmount)

    override suspend fun updateGoal(
        id: Int, name: String, description: String?,
        targetAmount: BigDecimal, currentAmount: BigDecimal,
        targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal = local.updateGoal(
        id, name, description, targetAmount, currentAmount, targetDate, monthlyAmount)

    override suspend fun deleteGoal(id: Int) = local.deleteGoal(id)

    override suspend fun getAnalytics(month: YearMonth, type: TransactionType): Analytics {
        val from = month.atDay(1)
        val to = month.atEndOfMonth()
        val (txs, sources, tags) = local.snapshotForAnalytics(from, to, type)

        val dailyMap = generateSequence(from) { d -> if (d.isBefore(to)) d.plusDays(1) else null }
            .associateWithTo(linkedMapOf()) { BigDecimal.ZERO }
        var total = BigDecimal.ZERO
        for (t in txs) {
            dailyMap.merge(t.date, t.amount, BigDecimal::add)
            total = total.add(t.amount)
        }
        val daily = dailyMap.map { (d, amt) -> DailyTotal(d, amt) }

        val sourceNames = sources.associate { it.id to it.name }
        val bySourceMap = mutableMapOf<Int, BigDecimal>()
        for (t in txs) bySourceMap.merge(t.sourceId, t.amount, BigDecimal::add)
        val bySource = bySourceMap.entries
            .map { (id, amt) -> SourceBucket(id, sourceNames[id] ?: "—", amt) }
            .sortedByDescending { it.amount }

        val byTag = if (type == TransactionType.EXPENSE) {
            val tagNames = tags.associate { it.id to it.name }
            val byTagMap = mutableMapOf<Int, BigDecimal>()
            for (t in txs) t.tagId?.let { byTagMap.merge(it, t.amount, BigDecimal::add) }
            byTagMap.entries
                .map { (id, amt) -> TagBucket(id, tagNames[id] ?: "—", amt) }
                .sortedByDescending { it.amount }
        } else null

        return Analytics(
            periodStart = from, periodEnd = to, type = type,
            total = total, daily = daily, bySource = bySource, byTag = byTag,
        )
    }

    override suspend fun getBankReports(): List<BankReport> = local.listBankReports()

    override suspend fun uploadBankReport(
        fileName: String, sizeBytes: Long, content: ByteArray,
    ): BankReport = local.uploadBankReport(fileName, sizeBytes)

    override suspend fun deleteBankReport(id: Int) = local.deleteBankReport(id)
    override suspend fun processBankReport(id: Int): BankReport = local.markReportProcessed(id)
}
