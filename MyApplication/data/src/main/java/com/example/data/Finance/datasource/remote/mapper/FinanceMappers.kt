package com.example.data.Finance.datasource.remote.mapper

import com.example.data.Finance.datasource.remote.dto.*
import com.example.domain.Finance.model.*
import java.time.LocalDate
import java.time.YearMonth

internal object FinanceMappers {

    fun SourceDto.toDomain(): Source =
        Source(id = id, name = name, type = SourceType.fromRaw(type))

    fun TagDto.toDomain(): Tag =
        Tag(id = id, name = name, totalAmountSpent = totalAmountSpent, monthlyLimit = monthlyLimit)

    fun TransactionDto.toDomain(): Transaction = Transaction(
        id = id,
        name = name,
        amount = amount,
        type = TransactionType.fromRaw(type),
        description = description,
        date = LocalDate.parse(transactionDate),
        sourceId = sourceId,
        tagId = tagId,
    )

    fun GoalDto.toDomain(): Goal = Goal(
        id = id,
        name = name,
        description = description,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        targetDate = targetDate?.let(LocalDate::parse),
        monthlyAmount = monthlyAmount,
    )

    fun ImportReportDto.toDomain(): ImportReport = ImportReport(
        fileName = fileName,
        sourceId = sourceId,
        imported = imported,
        skipped = skipped,
        total = total,
        firstDate = firstDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        lastDate  = lastDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        suggestedMonth = suggestedMonth?.let {
            runCatching { YearMonth.parse(it) }.getOrNull()
        },
    )

    fun AnalyticsResponseDto.toDomain(): Analytics = Analytics(
        periodStart = LocalDate.parse(periodStart),
        periodEnd   = LocalDate.parse(periodEnd),
        type = TransactionType.fromRaw(type),
        total = total,
        daily = daily.map { DailyTotal(LocalDate.parse(it.date), it.amount) },
        bySource = bySource.map { SourceBucket(it.sourceId, it.sourceName, it.amount) },
        byTag = byTag?.map { TagBucket(it.tagId, it.tagName, it.amount) },
    )
}
