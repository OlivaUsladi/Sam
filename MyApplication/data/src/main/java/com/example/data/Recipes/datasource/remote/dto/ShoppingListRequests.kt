package com.example.data.Recipes.datasource.remote.dto

data class CreateShoppingListRequestDto(
    val name: String
)

data class RenameShoppingListRequestDto(
    val name: String
)

data class AddShoppingListItemRequestDto(
    val description: String,
    val quantity: Double?,
    val unit: String?
)

data class UpdateShoppingListItemRequestDto(
    val description: String? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    val isChecked: Boolean? = null
)

data class CheckAllItemsRequestDto(
    val isChecked: Boolean
)

data class MergeShoppingListsRequestDto(
    val targetListId: Int,
    val sourceListIds: List<Int>
)

data class AddItemsFromRecipeRequestDto(
    val recipeId: Int,
    val ingredients: List<RecipeIngredientFromRecipeDto>
)

data class RecipeIngredientFromRecipeDto(
    val name: String,
    val amount: Double,
    val unit: String
)
