package com.example.data.Recipes.datasource.local.model

import com.example.domain.Recipes.model.ShoppingList
import java.time.LocalDateTime

data class ShoppingListEntity(
    val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val isCompleted: Boolean = false
) {
    fun toDomain(items: List<ShoppingListItemEntity>): ShoppingList {
        return ShoppingList(
            id = id,
            userId = userId,
            name = name,
            createdAt = createdAt,
            items = items.map { it.toDomain() }.toMutableList(),
            isCompleted = isCompleted
        )
    }
}