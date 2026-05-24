package com.example.domain.Finance.model

import java.math.BigDecimal
import java.time.LocalDate

//Данные для диаграмм
data class Analytics(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val type: TransactionType,
    val total: BigDecimal,
    val daily: List<DailyTotal>,
    val bySource: List<SourceBucket>,
    val byTag: List<TagBucket>?
)

data class DailyTotal(val date: LocalDate, val amount: BigDecimal)

data class SourceBucket(val sourceId: Int, val sourceName: String, val amount: BigDecimal)

data class TagBucket(val tagId: Int, val tagName: String, val amount: BigDecimal)
