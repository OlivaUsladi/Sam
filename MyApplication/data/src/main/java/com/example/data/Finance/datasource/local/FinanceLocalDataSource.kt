package com.example.data.Finance.datasource.local

import com.example.domain.Finance.model.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

class FinanceLocalDataSource {

    private val sources = mutableListOf<Source>()
    private val tags = mutableListOf<Tag>()
    private val transactions = mutableListOf<Transaction>()
    private val goals = mutableListOf<Goal>()
    private val bankReports = mutableListOf<BankReport>()

    private val sourceIds = AtomicInteger(0)
    private val tagIds = AtomicInteger(0)
    private val transactionIds = AtomicInteger(0)
    private val goalIds = AtomicInteger(0)
    private val bankReportIds = AtomicInteger(0)

    private val mutex = Mutex()

    init { seed() }

    private fun seed() {
        // Источники
        sources += Source(sourceIds.incrementAndGet(), "Наличные", SourceType.CASH)
        sources += Source(sourceIds.incrementAndGet(), "Сбер дебетовая", SourceType.CARD)
        sources += Source(sourceIds.incrementAndGet(), "Т-Банк", SourceType.BANK)

        // Тэги
        tags += Tag(tagIds.incrementAndGet(), "Продукты", BigDecimal.ZERO)
        tags += Tag(tagIds.incrementAndGet(), "Кафе", BigDecimal.ZERO)
        tags += Tag(tagIds.incrementAndGet(), "Транспорт", BigDecimal.ZERO)
        tags += Tag(tagIds.incrementAndGet(), "Кино", BigDecimal.ZERO)

        // Транзакции
        val today = LocalDate.now()
        addTxInternal("Стипендия", BigDecimal(8500), TransactionType.INCOME,
            "за май", today.withDayOfMonth(1), 2, null)
        addTxInternal("Магнит", BigDecimal(750), TransactionType.EXPENSE,
            "продукты на 3 дня", today.minusDays(2), 2, 1)
        addTxInternal("Перекрёсток", BigDecimal(1280), TransactionType.EXPENSE,
            null, today.minusDays(5), 2, 1)
        addTxInternal("Starbucks", BigDecimal(420), TransactionType.EXPENSE,
            "латте утром", today.minusDays(3), 1, 2)
        addTxInternal("Метро", BigDecimal(80), TransactionType.EXPENSE,
            null, today.minusDays(1), 1, 3)
        addTxInternal("Билет в кино", BigDecimal(550), TransactionType.EXPENSE,
            "вечер пятницы", today.minusDays(7), 2, 4)
        addTxInternal("Подработка", BigDecimal(3500), TransactionType.INCOME,
            "репетиторство", today.minusDays(4), 3, null)

        // Цели
        goals += Goal(
            id = goalIds.incrementAndGet(),
            name = "Поездка в Питер",
            description = "Лето, 5 дней",
            targetAmount = BigDecimal(40000),
            currentAmount = BigDecimal(8000),
            targetDate = today.plusMonths(3),
            monthlyAmount = BigDecimal(11000),
        )
        goals += Goal(
            id = goalIds.incrementAndGet(),
            name = "Ноутбук",
            description = "Подкопить на учёбу",
            targetAmount = BigDecimal(95000),
            currentAmount = BigDecimal(15000),
            targetDate = today.plusMonths(8),
            monthlyAmount = BigDecimal(10000),
        )

        recomputeTagTotals()
    }

    private fun addTxInternal(
        name: String, amount: BigDecimal, type: TransactionType,
        description: String?, date: LocalDate, sourceId: Int, tagId: Int?,
    ) {
        transactions += Transaction(
            id = transactionIds.incrementAndGet(),
            name = name,
            amount = amount,
            type = type,
            description = description,
            date = date,
            sourceId = sourceId,
            tagId = tagId,
        )
    }

    private fun recomputeTagTotals() {
        val sums = transactions
            .filter { it.type == TransactionType.EXPENSE && it.tagId != null }
            .groupBy { it.tagId!! }
            .mapValues { (_, txs) ->
                txs.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            }
        for (i in tags.indices) {
            val tag = tags[i]
            tags[i] = tag.copy(totalAmountSpent = sums[tag.id] ?: BigDecimal.ZERO)
        }
    }

    suspend fun listSources(): List<Source> = mutex.withLock { sources.sortedBy { it.name } }

    suspend fun createSource(name: String, type: SourceType): Source = mutex.withLock {
        require(sources.none { it.name.equals(name, ignoreCase = true) }) {
            "Источник с таким именем уже есть"
        }
        Source(sourceIds.incrementAndGet(), name.trim(), type).also { sources += it }
    }

    suspend fun updateSource(id: Int, name: String, type: SourceType): Source = mutex.withLock {
        val idx = sources.indexOfFirst { it.id == id }
        require(idx >= 0) { "Источник не найден" }
        Source(id, name.trim(), type).also { sources[idx] = it }
    }

    suspend fun deleteSource(id: Int) = mutex.withLock {
        require(transactions.none { it.sourceId == id }) {
            "У источника есть транзакции — сначала удалите их"
        }
        sources.removeAll { it.id == id }
        Unit
    }


    suspend fun listTags(): List<Tag> = mutex.withLock { tags.sortedBy { it.name } }

    suspend fun createTag(name: String): Tag = mutex.withLock {
        require(tags.none { it.name.equals(name, ignoreCase = true) }) {
            "Тэг с таким именем уже есть"
        }
        Tag(tagIds.incrementAndGet(), name.trim(), BigDecimal.ZERO).also { tags += it }
    }

    suspend fun updateTag(id: Int, name: String): Tag = mutex.withLock {
        val idx = tags.indexOfFirst { it.id == id }
        require(idx >= 0) { "Тэг не найден" }
        tags[idx].copy(name = name.trim()).also { tags[idx] = it }
    }

    suspend fun deleteTag(id: Int) = mutex.withLock {
        for (i in transactions.indices) {
            if (transactions[i].tagId == id) transactions[i] = transactions[i].copy(tagId = null)
        }
        tags.removeAll { it.id == id }
        recomputeTagTotals()
        Unit
    }

    suspend fun assignTagToTransactions(tagId: Int, ids: List<Int>) = mutex.withLock {
        require(tags.any { it.id == tagId }) { "Тэг не найден" }
        for (i in transactions.indices) {
            val tx = transactions[i]
            transactions[i] = when {
                tx.id in ids && tx.type == TransactionType.EXPENSE -> tx.copy(tagId = tagId)
                tx.tagId == tagId -> tx.copy(tagId = null)
                else -> tx
            }
        }
        recomputeTagTotals()
        Unit
    }

    suspend fun listTransactions(
        type: TransactionType?, from: LocalDate?, to: LocalDate?
    ): List<Transaction> = mutex.withLock {
        transactions
            .asSequence()
            .filter { type == null || it.type == type }
            .filter { from == null || !it.date.isBefore(from) }
            .filter { to == null || !it.date.isAfter(to) }
            .sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.id })
            .toList()
    }

    suspend fun listTransactionsByTag(tagId: Int): List<Transaction> = mutex.withLock {
        transactions.filter { it.tagId == tagId }
            .sortedWith(compareByDescending<Transaction> { it.date }.thenByDescending { it.id })
    }

    suspend fun createTransaction(
        name: String, amount: BigDecimal, type: TransactionType,
        description: String?, date: LocalDate, sourceId: Int, tagId: Int?,
    ): Transaction = mutex.withLock {
        require(sources.any { it.id == sourceId }) { "Источник не найден" }
        if (tagId != null) {
            require(type == TransactionType.EXPENSE) { "Тэг можно ставить только на расход" }
            require(tags.any { it.id == tagId }) { "Тэг не найден" }
        }
        Transaction(
            id = transactionIds.incrementAndGet(),
            name = name.trim(), amount = amount, type = type,
            description = description, date = date,
            sourceId = sourceId, tagId = tagId,
        ).also {
            transactions += it
            recomputeTagTotals()
        }
    }

    suspend fun updateTransaction(
        id: Int, name: String, amount: BigDecimal, type: TransactionType,
        description: String?, date: LocalDate, sourceId: Int, tagId: Int?,
    ): Transaction = mutex.withLock {
        val idx = transactions.indexOfFirst { it.id == id }
        require(idx >= 0) { "Транзакция не найдена" }
        require(sources.any { it.id == sourceId }) { "Источник не найден" }
        if (tagId != null) {
            require(type == TransactionType.EXPENSE) { "Тэг можно ставить только на расход" }
            require(tags.any { it.id == tagId }) { "Тэг не найден" }
        }
        Transaction(
            id = id, name = name.trim(), amount = amount, type = type,
            description = description, date = date,
            sourceId = sourceId, tagId = tagId,
        ).also {
            transactions[idx] = it
            recomputeTagTotals()
        }
    }

    suspend fun deleteTransaction(id: Int) = mutex.withLock {
        transactions.removeAll { it.id == id }
        recomputeTagTotals()
        Unit
    }

    suspend fun listGoals(): List<Goal> = mutex.withLock { goals.toList() }

    suspend fun getGoal(id: Int): Goal = mutex.withLock {
        goals.firstOrNull { it.id == id } ?: error("Цель не найдена")
    }

    suspend fun createGoal(
        name: String, description: String?, targetAmount: BigDecimal,
        targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal = mutex.withLock {
        Goal(
            id = goalIds.incrementAndGet(),
            name = name.trim(),
            description = description,
            targetAmount = targetAmount,
            currentAmount = BigDecimal.ZERO,
            targetDate = targetDate,
            monthlyAmount = monthlyAmount ?: recommendedMonthly(
                targetAmount, BigDecimal.ZERO, targetDate),
        ).also { goals += it }
    }

    suspend fun updateGoal(
        id: Int, name: String, description: String?,
        targetAmount: BigDecimal, currentAmount: BigDecimal,
        targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal = mutex.withLock {
        val idx = goals.indexOfFirst { it.id == id }
        require(idx >= 0) { "Цель не найдена" }
        Goal(
            id = id,
            name = name.trim(),
            description = description,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            targetDate = targetDate,
            monthlyAmount = monthlyAmount ?: recommendedMonthly(
                targetAmount, currentAmount, targetDate),
        ).also { goals[idx] = it }
    }

    suspend fun deleteGoal(id: Int) = mutex.withLock {
        goals.removeAll { it.id == id }
        Unit
    }

    private fun recommendedMonthly(
        target: BigDecimal, current: BigDecimal, targetDate: LocalDate?,
    ): BigDecimal? {
        if (targetDate == null) return null
        val months = java.time.temporal.ChronoUnit.MONTHS.between(
            LocalDate.now().withDayOfMonth(1), targetDate.withDayOfMonth(1)
        )
        if (months <= 0) return null
        val remaining = target.subtract(current)
        if (remaining.signum() <= 0) return BigDecimal.ZERO
        return remaining.divide(BigDecimal.valueOf(months), 0, java.math.RoundingMode.CEILING)
    }

    suspend fun listBankReports(): List<BankReport> =
        mutex.withLock { bankReports.sortedByDescending { it.uploadedAt } }

    suspend fun uploadBankReport(fileName: String, sizeBytes: Long): BankReport = mutex.withLock {
        BankReport(
            id = bankReportIds.incrementAndGet(),
            fileName = fileName,
            sizeBytes = sizeBytes,
            processed = false,
            uploadedAt = LocalDateTime.now(),
        ).also { bankReports += it }
    }

    suspend fun deleteBankReport(id: Int) = mutex.withLock {
        bankReports.removeAll { it.id == id }
        Unit
    }

    suspend fun markReportProcessed(id: Int): BankReport = mutex.withLock {
        val idx = bankReports.indexOfFirst { it.id == id }
        require(idx >= 0) { "Отчёт не найден" }
        bankReports[idx].copy(processed = true).also { bankReports[idx] = it }
    }


    suspend fun snapshotForAnalytics(
        from: LocalDate, to: LocalDate, type: TransactionType,
    ): Triple<List<Transaction>, List<Source>, List<Tag>> = mutex.withLock {
        Triple(
            transactions.filter { it.type == type && !it.date.isBefore(from) && !it.date.isAfter(to) },
            sources.toList(),
            tags.toList(),
        )
    }
}
