package com.example.data.Recipes.datasource.local.room.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shop_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListLocalEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["listId"])],
)
data class ShoppingListItemLocalEntity(
    @PrimaryKey val id: Int,
    val listId: Int,
    val description: String,
    val isChecked: Boolean = false,
    val quantity: Double? = null,
    val unit: String? = null,
    val pendingCreate: Boolean = false,
    val pendingUpdate: Boolean = false,
    val pendingDelete: Boolean = false,
)
