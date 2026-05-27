package com.example.data.Finance.datasource.remote.dto

import java.math.BigDecimal

data class AnalyticsResponseDto(
    val periodStart: String,
    val periodEnd: String,
    val type: String,
    val total: BigDecimal,
    val daily: List<DailyTotalPointDto>,
    val bySource: List<SourceBucketDto>,
    val byTag: List<TagBucketDto>?
)

data class DailyTotalPointDto(val date: String, val amount: BigDecimal)
data class SourceBucketDto(val sourceId: Int, val sourceName: String, val amount: BigDecimal)
data class TagBucketDto(val tagId: Int, val tagName: String, val amount: BigDecimal)
