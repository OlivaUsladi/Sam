package com.example.domain.Finance.model

import java.time.LocalDateTime

//Для выписок из банка
data class BankReport(
    val id: Int,
    val fileName: String,
    val sizeBytes: Long,
    val processed: Boolean,
    val uploadedAt: LocalDateTime
)
