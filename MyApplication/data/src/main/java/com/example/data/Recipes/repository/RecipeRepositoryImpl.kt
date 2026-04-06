package com.example.data.Recipes.repository

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.model.*
import com.example.domain.Recipes.model.*
import com.example.domain.Recipes.repository.RecipeRepository
import java.time.LocalDateTime

class RecipeRepositoryImpl(
    private val localDataSource: RecipeLocalDataSource,
    private val userId: Int = 1
) : RecipeRepository {

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private suspend fun getCategoriesForRecipe(recipeId: Int): List<CategoryEntity> {
        val categoryIds = localDataSource.getRecipeCategoryIds(recipeId)
        return localDataSource.getCategories().filter { it.id in categoryIds }
    }

    private suspend fun getGroceryItemsForRecipe(recipeId: Int): List<GroceryItemEntity> {
        val groceryItemIds = localDataSource.getRecipeGroceryItemIds(recipeId)
        return localDataSource.getGroceryItems().filter { it.id in groceryItemIds }
    }

    private suspend fun isRecipeFavorite(recipeId: Int): Boolean =
        localDataSource.isFavorite(userId, recipeId)

    private suspend fun isRecipeLiked(recipeId: Int): Boolean =
        localDataSource.isLiked(userId, recipeId)

    // ========== ОСНОВНЫЕ МЕТОДЫ ==========

    override suspend fun getRecipes(): List<Recipe> {
        val recipeEntities = localDataSource.getRecipes()
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val categories = getCategoriesForRecipe(entity.id)
            val groceryItems = getGroceryItemsForRecipe(entity.id)

            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                groceries = allGroceries.map { it.toDomain() },
                groceryItems = groceryItems.map { it.toDomain() },
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(entity.id),
                isLiked = isRecipeLiked(entity.id)
            )
        }
    }

    override suspend fun getRecipeById(recipeId: Int): Recipe? {
        val entity = localDataSource.getRecipeById(recipeId) ?: return null
        val categories = getCategoriesForRecipe(recipeId)
        val groceryItems = getGroceryItemsForRecipe(recipeId)
        val allGroceries = localDataSource.getGroceries()

        return Recipe(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            categories = categories.map { it.toDomain() },
            groceries = allGroceries.map { it.toDomain() },
            groceryItems = groceryItems.map { it.toDomain() },
            author = entity.author,
            previewImageUrl = entity.previewImageUrl,
            cookingTimeMinutes = entity.cookingTimeMinutes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            likesCount = entity.likesCount,
            isFavorite = isRecipeFavorite(recipeId),
            isLiked = isRecipeLiked(recipeId)
        )
    }

    override suspend fun getRecipeContent(recipeId: Int): RecipeContent? {
        val contentEntity = localDataSource.getRecipeContent(recipeId) ?: return null
        val groceryItemEntities = getGroceryItemsForRecipe(recipeId)

        return RecipeContent(
            recipeId = recipeId,
            groceryItems = groceryItemEntities.map { it.toDomain() },
            cookingSteps = contentEntity.cookingSteps,
            tips = contentEntity.tips
        )
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        val recipeEntities = localDataSource.searchRecipes(query)
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val categories = getCategoriesForRecipe(entity.id)
            val groceryItems = getGroceryItemsForRecipe(entity.id)

            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                groceries = allGroceries.map { it.toDomain() },
                groceryItems = groceryItems.map { it.toDomain() },
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(entity.id),
                isLiked = isRecipeLiked(entity.id)
            )
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<Recipe> {
        val recipeEntities = localDataSource.getRecipesByCategory(categoryId)
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val categories = getCategoriesForRecipe(entity.id)
            val groceryItems = getGroceryItemsForRecipe(entity.id)

            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                groceries = allGroceries.map { it.toDomain() },
                groceryItems = groceryItems.map { it.toDomain() },
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(entity.id),
                isLiked = isRecipeLiked(entity.id)
            )
        }
    }

    override suspend fun getRecipeByGrocery(groceryId: Int): List<Recipe> = emptyList()

    override suspend fun getCategories(): List<Category> =
        localDataSource.getCategories().map { it.toDomain() }

    override suspend fun getGroceries(): List<Grocery> =
        localDataSource.getGroceries().map { it.toDomain() }

    override suspend fun getGroceryItems(): List<GroceryItem> =
        localDataSource.getGroceryItems().map { it.toDomain() }

    override suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<Recipe> {
        if (groceryItemIds.isEmpty()) return emptyList()

        val recipeEntities = localDataSource.getRecipesByGroceryItems(groceryItemIds)
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val categories = getCategoriesForRecipe(entity.id)
            val groceryItems = getGroceryItemsForRecipe(entity.id)

            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                groceries = allGroceries.map { it.toDomain() },
                groceryItems = groceryItems.map { it.toDomain() },
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(entity.id),
                isLiked = isRecipeLiked(entity.id)
            )
        }
    }

    override suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<Recipe> {
        if (groceryItemIds.isEmpty()) return emptyList()

        val recipeEntities = localDataSource.getRecipesByExactGroceryItems(groceryItemIds)
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val categories = getCategoriesForRecipe(entity.id)
            val groceryItems = getGroceryItemsForRecipe(entity.id)

            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                groceries = allGroceries.map { it.toDomain() },
                groceryItems = groceryItems.map { it.toDomain() },
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(entity.id),
                isLiked = isRecipeLiked(entity.id)
            )
        }
    }

    override suspend fun getRecipesWithMissingItems(groceryItemIds: List<Int>): List<Recipe> {
        if (groceryItemIds.isEmpty()) return emptyList()
        val allRecipes = getRecipes()
        return allRecipes.filter { recipe ->
            recipe.groceryItems.any { it.id !in groceryItemIds }
        }
    }

    // ========== ИЗБРАННОЕ ==========

    override suspend fun getFavoriteRecipes(userId: Int): List<Recipe> {
        val favorites = localDataSource.getFavorites(userId)
        val favoriteRecipeIds = favorites.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in favoriteRecipeIds }.map { it.copy(isFavorite = true) }
    }

    override suspend fun addToFavorites(userId: Int, recipeId: Int): Favourite {
        localDataSource.addFavorite(userId, recipeId)
        return Favourite(userId, recipeId)
    }

    override suspend fun removeFromFavorites(userId: Int, recipeId: Int): Boolean =
        localDataSource.removeFavorite(userId, recipeId)

    override suspend fun isRecipeFavorite(userId: Int, recipeId: Int): Boolean =
        localDataSource.isFavorite(userId, recipeId)

    // ========== ЛАЙКИ ==========

    override suspend fun getLikedRecipes(userId: Int): List<Recipe> {
        val likedRecipes = localDataSource.getUserLikes(userId)
        val likedRecipeIds = likedRecipes.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in likedRecipeIds }.map { it.copy(isLiked = true) }
    }

    override suspend fun addLike(userId: Int, recipeId: Int): Like {
        localDataSource.addLike(userId, recipeId)
        return Like(userId, recipeId)
    }

    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean =
        localDataSource.removeLike(userId, recipeId)

    override suspend fun isRecipeLiked(userId: Int, recipeId: Int): Boolean =
        localDataSource.isLiked(userId, recipeId)

    override suspend fun getLikesCount(recipeId: Int): Int =
        localDataSource.getLikesCount(recipeId)

    // ========== СПИСКИ ПОКУПОК (ЗАГЛУШКИ) ==========

    override suspend fun getShoppingLists(userId: Int): List<ShoppingList> = emptyList()
    override suspend fun getShoppingListById(listId: Int): ShoppingList? = null
    override suspend fun createShoppingList(userId: Int, name: String, recipeId: Int?): ShoppingList =
        ShoppingList(0, userId, name, LocalDateTime.now(), mutableListOf(), false)
    override suspend fun updateShoppingListName(listId: Int, newName: String): ShoppingList? = null
    override suspend fun deleteShoppingList(listId: Int): Boolean = false
    override suspend fun getShoppingListItems(listId: Int): List<ShoppingListItem> = emptyList()
    override suspend fun addItemToList(listId: Int, item: ShoppingListItem): ShoppingListItem = item
    override suspend fun addItemsFromRecipe(listId: Int, recipeId: Int, groceryItems: List<GroceryItem>): List<ShoppingListItem> = emptyList()
    override suspend fun updateShoppingListItem(itemId: Int, isChecked: Boolean): ShoppingListItem? = null
    override suspend fun updateShoppingListItemDetails(itemId: Int, description: String, quantity: Double?, unit: String?): ShoppingListItem? = null
    override suspend fun removeShoppingListItem(itemId: Int): Boolean = false
    override suspend fun mergeShoppingLists(targetListId: Int, sourceListIds: List<Int>): ShoppingList? = null
    override suspend fun clearCompletedItems(listId: Int): Boolean = false
}