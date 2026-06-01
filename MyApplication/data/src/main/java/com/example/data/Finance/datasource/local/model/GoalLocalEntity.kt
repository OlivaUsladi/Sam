package com.example.data.Finance.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fin_goals")
data class GoalLocalEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val description: String?,
    val targetAmount: String,
    val currentAmount: String,
    val targetDate: String?,
    val monthlyAmount: String?,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)
