package com.example.domain.Recipes.repository

import com.example.domain.Recipes.model.Category
import com.example.domain.Recipes.model.Favourite
import com.example.domain.Recipes.model.Grocery
import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.Like
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.model.RecipeContent
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.model.ShoppingListItem

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun getRecipeById(recipeId: Int): Recipe?
    suspend fun getRecipeContent(recipeId: Int): RecipeContent?
    suspend fun searchRecipes(query: String): List<Recipe>
    suspend fun getRecipesByCategory(categoryId: Int): List<Recipe>
    suspend fun getRecipeByGrocery(groceryId: Int): List<Recipe>
    suspend fun getCategories(): List<Category>
    suspend fun getGroceries(): List<Grocery>


    suspend fun getGroceryItems(): List<GroceryItem>
    suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<Recipe>
    suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<Recipe>
    suspend fun getRecipesWithMissingItems(
        groceryItemIds: List<Int>
    ): List<Recipe>


    suspend fun getFavoriteRecipes(userId: Int): List<Recipe>
    suspend fun addToFavorites(userId: Int, recipeId: Int): Favourite
    suspend fun removeFromFavorites(userId: Int, recipeId: Int): Boolean
    suspend fun isRecipeFavorite(userId: Int, recipeId: Int): Boolean

    suspend fun getLikedRecipes(userId: Int): List<Recipe>
    suspend fun addLike(userId: Int, recipeId: Int): Like
    suspend fun removeLike(userId: Int, recipeId: Int): Boolean
    suspend fun isRecipeLiked(userId: Int, recipeId: Int): Boolean
    suspend fun getLikesCount(recipeId: Int): Int



    suspend fun getShoppingLists(userId: Int): List<ShoppingList>
    suspend fun getShoppingListById(listId: Int): ShoppingList?
    suspend fun createShoppingList(userId: Int, name: String, recipeId: Int? = null): ShoppingList
    suspend fun updateShoppingListName(listId: Int, newName: String): ShoppingList?
    suspend fun deleteShoppingList(listId: Int): Boolean
    suspend fun getShoppingListItems(listId: Int): List<ShoppingListItem>
    suspend fun addItemToList(listId: Int, item: ShoppingListItem): ShoppingListItem
    suspend fun addItemsFromRecipe(listId: Int, recipeId: Int, groceryItems: List<GroceryItem>): List<ShoppingListItem>
    suspend fun updateShoppingListItem(itemId: Int, isChecked: Boolean): ShoppingListItem?
    suspend fun updateShoppingListItemDetails(itemId: Int, description: String, quantity: Double?, unit: String?): ShoppingListItem?
    suspend fun removeShoppingListItem(itemId: Int): Boolean
    suspend fun mergeShoppingLists(targetListId: Int, sourceListIds: List<Int>): ShoppingList?
    suspend fun clearCompletedItems(listId: Int): Boolean
}