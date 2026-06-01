package com.example.data.Finance.datasource.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "fin_transactions")
data class TransactionLocalEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val amount: String,
    val type: String,
    val description: String?,
    val transactionDate: String,
    val sourceId: Int,
    val tagId: Int?,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)
