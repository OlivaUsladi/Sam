package com.example.data.Recipes.model

import com.example.domain.Recipes.model.GroceryItem

data class GroceryItemEntity(
    val id: Int,
    val groceryId: Int,
    val name: String,
    val defaultUnit: String
) {
    fun toDomain(): GroceryItem {
        return GroceryItem(
            id = id,
            groceryId = groceryId,
            name = name,
            defaultUnit = defaultUnit
        )
    }
}