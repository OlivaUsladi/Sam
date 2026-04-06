package com.example.data.Recipes.repository

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.model.CategoryEntity
import com.example.data.Recipes.model.GroceryEntity
import com.example.domain.Recipes.model.*
import com.example.domain.Recipes.repository.RecipeRepository
import java.time.LocalDateTime

class RecipeRepositoryImpl(
    private val localDataSource: RecipeLocalDataSource,
    private val userId: Int = 1
) : RecipeRepository {

    private suspend fun getCategoriesForRecipe(recipeId: Int, allCategories: List<CategoryEntity>): List<CategoryEntity> {
        val categoryIds = localDataSource.getRecipeCategoryIds(recipeId)
        return allCategories.filter { it.id in categoryIds }
    }

    private suspend fun getGroceriesForRecipe(recipeId: Int, allGroceries: List<GroceryEntity>): List<GroceryEntity> {
        val groceryIds = localDataSource.getRecipeGroceryIds(recipeId)
        return allGroceries.filter { it.id in groceryIds }
    }

    override suspend fun getRecipes(): List<Recipe> {
        val recipeEntities = localDataSource.getRecipes()
        val allCategories = localDataSource.getCategories()
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val recipeCategories = getCategoriesForRecipe(entity.id, allCategories)
            val recipeGroceries = getGroceriesForRecipe(entity.id, allGroceries)

            entity.toDomain(
                categories = recipeCategories,
                groceries = recipeGroceries,
                isFavorite = isRecipeFavorite(userId, entity.id),
                isLiked = isRecipeLiked(userId, entity.id)
            )
        }
    }

    override suspend fun getRecipeById(recipeId: Int): Recipe? {
        val entity = localDataSource.getRecipeById(recipeId) ?: return null
        val allCategories = localDataSource.getCategories()
        val allGroceries = localDataSource.getGroceries()

        val recipeCategories = getCategoriesForRecipe(recipeId, allCategories)
        val recipeGroceries = getGroceriesForRecipe(recipeId, allGroceries)

        return entity.toDomain(
            categories = recipeCategories,
            groceries = recipeGroceries,
            isFavorite = isRecipeFavorite(userId, recipeId),
            isLiked = isRecipeLiked(userId, recipeId)
        )
    }

    private suspend fun getRecipeIngredientsForRecipe(recipeId: Int): List<RecipeIngredient> {
        val crossRefs = localDataSource.getRecipeIngredientsCrossRef(recipeId)
        val allIngredients = localDataSource.getAllIngredients()

        return crossRefs.mapNotNull { cross ->
            val ingredient = allIngredients.find { it.id == cross.ingredientId }
            ingredient?.let {
                RecipeIngredient(
                    ingredient = it.toDomain(),
                    amount = cross.amount,
                    unit = cross.unit
                )
            }
        }
    }

    override suspend fun getRecipeContent(recipeId: Int): RecipeContent? {
        val contentEntity = localDataSource.getRecipeContent(recipeId) ?: return null
        val ingredients = getRecipeIngredientsForRecipe(recipeId)
        return contentEntity.toDomain(ingredients)
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        val recipeEntities = localDataSource.searchRecipes(query)
        val allCategories = localDataSource.getCategories()
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val recipeCategories = getCategoriesForRecipe(entity.id, allCategories)
            val recipeGroceries = getGroceriesForRecipe(entity.id, allGroceries)

            entity.toDomain(
                categories = recipeCategories,
                groceries = recipeGroceries,
                isFavorite = isRecipeFavorite(userId, entity.id),
                isLiked = isRecipeLiked(userId, entity.id)
            )
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<Recipe> {
        val recipeEntities = localDataSource.getRecipesByCategory(categoryId)
        val allCategories = localDataSource.getCategories()
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val recipeCategories = getCategoriesForRecipe(entity.id, allCategories)
            val recipeGroceries = getGroceriesForRecipe(entity.id, allGroceries)

            entity.toDomain(
                categories = recipeCategories,
                groceries = recipeGroceries,
                isFavorite = isRecipeFavorite(userId, entity.id),
                isLiked = isRecipeLiked(userId, entity.id)
            )
        }
    }

    override suspend fun getRecipeByGrocery(groceryId: Int): List<Recipe> {
        val recipeEntities = localDataSource.getRecipesByGrocery(groceryId)
        val allCategories = localDataSource.getCategories()
        val allGroceries = localDataSource.getGroceries()

        return recipeEntities.map { entity ->
            val recipeCategories = getCategoriesForRecipe(entity.id, allCategories)
            val recipeGroceries = getGroceriesForRecipe(entity.id, allGroceries)

            entity.toDomain(
                categories = recipeCategories,
                groceries = recipeGroceries,
                isFavorite = isRecipeFavorite(userId, entity.id),
                isLiked = isRecipeLiked(userId, entity.id)
            )
        }
    }

    override suspend fun getCategories(): List<Category> =
        localDataSource.getCategories().map { it.toDomain() }

    override suspend fun getGroceries(): List<Grocery> =
        localDataSource.getGroceries().map { it.toDomain() }

    override suspend fun getFavoriteRecipes(userId: Int): List<Recipe> {
        val favorites = localDataSource.getFavorites(userId)
        val favoriteRecipeIds = favorites.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in favoriteRecipeIds }
            .map { it.copy(isFavorite = true) }
    }

    override suspend fun addToFavorites(userId: Int, recipeId: Int): Favourite {
        val favoriteEntity = localDataSource.addFavorite(userId, recipeId)
        return favoriteEntity.toDomain()
    }

    override suspend fun removeFromFavorites(userId: Int, recipeId: Int): Boolean =
        localDataSource.removeFavorite(userId, recipeId)

    override suspend fun isRecipeFavorite(userId: Int, recipeId: Int): Boolean =
        localDataSource.isFavorite(userId, recipeId)

    override suspend fun getLikedRecipes(userId: Int): List<Recipe> {
        val likedRecipes = localDataSource.getUserLikes(userId)
        val likedRecipeIds = likedRecipes.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in likedRecipeIds }
            .map { it.copy(isLiked = true) }
    }

    override suspend fun addLike(userId: Int, recipeId: Int): Like {
        val likeEntity = localDataSource.addLike(userId, recipeId)
        return likeEntity.toDomain()
    }

    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean =
        localDataSource.removeLike(userId, recipeId)

    override suspend fun isRecipeLiked(userId: Int, recipeId: Int): Boolean =
        localDataSource.isLiked(userId, recipeId)

    override suspend fun getLikesCount(recipeId: Int): Int =
        localDataSource.getLikesCount(recipeId)

    //-------------------------------------------------------------
    // Shopping List методы ДОДЕЛАТЬ!!!!!!!!!!
    //-------------------------------------------------------------

    override suspend fun getShoppingLists(userId: Int): List<ShoppingList> = emptyList()

    override suspend fun getShoppingListById(listId: Int): ShoppingList? = null

    override suspend fun createShoppingList(
        userId: Int,
        name: String,
        recipeId: Int?
    ): ShoppingList = ShoppingList(
        id = 0,
        userId = userId,
        name = name,
        createdAt = LocalDateTime.now(),
        items = mutableListOf(),
        isCompleted = false
    )

    override suspend fun updateShoppingListName(
        listId: Int,
        newName: String
    ): ShoppingList? = null

    override suspend fun deleteShoppingList(listId: Int): Boolean = false

    override suspend fun getShoppingListItems(listId: Int): List<ShoppingListItem> = emptyList()

    override suspend fun addItemToList(
        listId: Int,
        item: ShoppingListItem
    ): ShoppingListItem = item

    override suspend fun addItemsFromRecipe(
        listId: Int,
        recipeId: Int,
        ingredients: List<Ingredient>
    ): List<ShoppingListItem> = emptyList()

    override suspend fun updateShoppingListItem(
        itemId: Int,
        isChecked: Boolean
    ): ShoppingListItem? = null

    override suspend fun updateShoppingListItemDetails(
        itemId: Int,
        description: String,
        quantity: Double?,
        unit: String?
    ): ShoppingListItem? = null

    override suspend fun removeShoppingListItem(itemId: Int): Boolean = false

    override suspend fun mergeShoppingLists(
        targetListId: Int,
        sourceListIds: List<Int>
    ): ShoppingList? = null

    override suspend fun clearCompletedItems(listId: Int): Boolean = false
}