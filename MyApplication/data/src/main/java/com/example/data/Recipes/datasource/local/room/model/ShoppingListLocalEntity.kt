package com.example.data.Recipes.datasource.local.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_lists")
data class ShoppingListLocalEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: String,
    val isCompleted: Boolean = false,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)
