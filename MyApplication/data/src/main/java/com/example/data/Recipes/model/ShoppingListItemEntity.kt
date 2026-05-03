package com.example.data.Recipes.model

import com.example.domain.Recipes.model.ShoppingListItem

data class ShoppingListItemEntity(
    val id: Int,
    val shoppingListId: Int,
    val description: String,
    val isChecked: Boolean = false,
    val quantity: Double? = null,
    val unit: String? = null
) {
    fun toDomain(): ShoppingListItem {
        return ShoppingListItem(
            id = id,
            description = description,
            isChecked = isChecked
        )
    }
}