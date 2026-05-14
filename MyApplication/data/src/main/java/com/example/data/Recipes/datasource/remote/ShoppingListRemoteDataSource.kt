package com.example.data.Recipes.datasource.remote

import com.example.data.Recipes.datasource.remote.api.ShoppingListApiService
import com.example.data.Recipes.datasource.remote.dto.AddItemsFromRecipeRequestDto
import com.example.data.Recipes.datasource.remote.dto.AddShoppingListItemRequestDto
import com.example.data.Recipes.datasource.remote.dto.CheckAllItemsRequestDto
import com.example.data.Recipes.datasource.remote.dto.CreateShoppingListRequestDto
import com.example.data.Recipes.datasource.remote.dto.MergeShoppingListsRequestDto
import com.example.data.Recipes.datasource.remote.dto.RecipeIngredientFromRecipeDto
import com.example.data.Recipes.datasource.remote.dto.RenameShoppingListRequestDto
import com.example.data.Recipes.datasource.remote.dto.ShoppingListItemResponseDto
import com.example.data.Recipes.datasource.remote.dto.ShoppingListResponseDto
import com.example.data.Recipes.datasource.remote.dto.UpdateShoppingListItemRequestDto

class ShoppingListRemoteDataSource(
    private val apiService: ShoppingListApiService
) {

    suspend fun getShoppingLists(userId: Int): List<ShoppingListResponseDto> =
        apiService.getShoppingLists(userId)

    suspend fun getShoppingListById(listId: Int, userId: Int): ShoppingListResponseDto =
        apiService.getShoppingListById(listId, userId)

    suspend fun createShoppingList(userId: Int, name: String): ShoppingListResponseDto =
        apiService.createShoppingList(userId, CreateShoppingListRequestDto(name))

    suspend fun renameShoppingList(listId: Int, userId: Int, name: String): ShoppingListResponseDto =
        apiService.renameShoppingList(listId, userId, RenameShoppingListRequestDto(name))

    suspend fun deleteShoppingList(listId: Int, userId: Int): Boolean =
        apiService.deleteShoppingList(listId, userId).isSuccessful

    suspend fun addItemToList(
        listId: Int,
        userId: Int,
        body: AddShoppingListItemRequestDto
    ): ShoppingListItemResponseDto =
        apiService.addItemToList(listId, userId, body)

    suspend fun addItemsFromRecipe(
        listId: Int,
        userId: Int,
        recipeId: Int,
        ingredients: List<RecipeIngredientFromRecipeDto>
    ): List<ShoppingListItemResponseDto> =
        apiService.addItemsFromRecipe(
            listId,
            userId,
            AddItemsFromRecipeRequestDto(recipeId, ingredients)
        )

    suspend fun updateItem(
        itemId: Int,
        userId: Int,
        body: UpdateShoppingListItemRequestDto
    ): ShoppingListItemResponseDto =
        apiService.updateItem(itemId, userId, body)

    suspend fun deleteItem(itemId: Int, userId: Int): Boolean =
        apiService.deleteItem(itemId, userId).isSuccessful

    suspend fun checkAllItems(listId: Int, userId: Int, isChecked: Boolean): Boolean =
        apiService.checkAllItems(listId, userId, CheckAllItemsRequestDto(isChecked)).isSuccessful

    suspend fun clearCompletedItems(listId: Int, userId: Int): Boolean =
        apiService.clearCompletedItems(listId, userId).isSuccessful

    suspend fun mergeShoppingLists(
        targetListId: Int,
        sourceListIds: List<Int>,
        userId: Int
    ): ShoppingListResponseDto =
        apiService.mergeShoppingLists(
            userId,
            MergeShoppingListsRequestDto(targetListId, sourceListIds)
        )
}
