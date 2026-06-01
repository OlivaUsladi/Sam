package com.example.data.Finance.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fin_tags")
data class TagLocalEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val totalAmountSpent: String,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)
