package com.example.data.Recipes.datasource.remote.mapper

import com.example.data.Recipes.datasource.remote.dto.ShoppingListItemResponseDto
import com.example.data.Recipes.datasource.remote.dto.ShoppingListResponseDto
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.model.ShoppingListItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ShoppingListNetworkMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun mapToDomain(dto: ShoppingListResponseDto): ShoppingList {
        return ShoppingList(
            id = dto.id,
            userId = dto.userId,
            name = dto.name,
            createdAt = parseDateOrNow(dto.createdAt),
            items = dto.items.map { mapToDomain(it) }.toMutableList(),
            isCompleted = dto.isCompleted
        )
    }

    fun mapToDomain(dto: ShoppingListItemResponseDto): ShoppingListItem {
        return ShoppingListItem(
            id = dto.id,
            description = dto.description,
            isChecked = dto.isChecked,
            quantity = dto.quantity,
            unit = dto.unit
        )
    }

    private fun parseDateOrNow(value: String?): LocalDateTime {
        if (value.isNullOrBlank()) return LocalDateTime.now()
        return try {
            LocalDateTime.parse(value, dateFormatter)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}