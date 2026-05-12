package com.example.data.Recipes.repository

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.model.CategoryEntity
import com.example.data.Recipes.datasource.remote.RecipeRemoteDataSource
import com.example.data.Recipes.datasource.remote.mapper.RecipeNetworkMapper
import com.example.domain.Recipes.model.*
import com.example.domain.Recipes.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val localDataSource: RecipeLocalDataSource,
    private val remoteDataSource: RecipeRemoteDataSource,
    private val userId: Int = 1
) : RecipeRepository {


    private suspend fun getCategoriesForRecipe(recipeId: Int): List<CategoryEntity> {
        val categoryIds = localDataSource.getRecipeCategoryIds(recipeId)
        return localDataSource.getCategories().filter { it.id in categoryIds }
    }

    private suspend fun getIngredientsForRecipe(recipeId: Int): List<RecipeIngredient> {
        val crossEntities = localDataSource.getRecipeGroceryItemsCrossRef(recipeId)
        val allGroceryItems = localDataSource.getGroceryItems()

        return crossEntities.mapNotNull { cross ->
            val groceryItemEntity = allGroceryItems.find { it.id == cross.groceryItemId }
            if (groceryItemEntity != null) {
                RecipeIngredient(
                    groceryItem = groceryItemEntity.toDomain(),
                    amount = cross.amount,
                    unit = cross.unit
                )
            } else null
        }
    }


    override suspend fun getRecipes(): List<Recipe> {
        return try {
            val recipesDto = remoteDataSource.getAllRecipesForUser(userId)
            recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {
            // Если сеть недоступна — локальная БД
            val recipeEntities = localDataSource.getRecipes()
            recipeEntities.map { entity ->
                val categories = getCategoriesForRecipe(entity.id)
                val ingredients = getIngredientsForRecipe(entity.id)
                Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    categories = categories.map { it.toDomain() },
                    ingredients = ingredients,
                    author = entity.author,
                    previewImageUrl = entity.previewImageUrl,
                    cookingTimeMinutes = entity.cookingTimeMinutes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    likesCount = entity.likesCount,
                    isFavorite = isRecipeFavorite(userId,entity.id),
                    isLiked = isRecipeLiked(userId,entity.id)
                )
            }
        }
    }

    override suspend fun getRecipeById(recipeId: Int): Recipe? {
        return try {
            val recipeDto = remoteDataSource.getRecipeByIdForUser(recipeId, userId)
            recipeDto?.let { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {

            val entity = localDataSource.getRecipeById(recipeId) ?: return null
            val categories = getCategoriesForRecipe(recipeId)
            val ingredients = getIngredientsForRecipe(recipeId)
            Recipe(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                categories = categories.map { it.toDomain() },
                ingredients = ingredients,
                author = entity.author,
                previewImageUrl = entity.previewImageUrl,
                cookingTimeMinutes = entity.cookingTimeMinutes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                likesCount = entity.likesCount,
                isFavorite = isRecipeFavorite(userId,recipeId),
                isLiked = isRecipeLiked(userId,recipeId)
            )
        }
    }

    override suspend fun getRecipeContent(recipeId: Int): RecipeContent? {
        return try {

            val recipeDto = remoteDataSource.getRecipeByIdForUser(recipeId, userId)
            if (recipeDto != null) {
                val ingredients = recipeDto.ingredients.map { ingredientDto ->
                    RecipeIngredient(
                        groceryItem = GroceryItem(
                            id = ingredientDto.groceryItem.id,
                            groceryId = ingredientDto.groceryItem.groceryId,
                            name = ingredientDto.groceryItem.name,
                            defaultUnit = ingredientDto.groceryItem.defaultUnit
                        ),
                        amount = ingredientDto.amount.toDouble(),
                        unit = ingredientDto.unit
                    )
                }

                val cookingSteps = recipeDto.cookingSteps.map { stepDto ->
                    when (stepDto.type) {
                        "paragraph" -> ContentBlock.Paragraph(
                            text = stepDto.text ?: "",
                            style = parseTextStyle(stepDto.style),
                            size = stepDto.size ?: 16,
                            area = parseTextArea(stepDto.area)
                        )
                        "image" -> ContentBlock.Image(
                            imageId = stepDto.imageId ?: 0,
                            url = stepDto.url ?: "",
                            width = stepDto.width,
                            height = stepDto.height
                        )
                        else -> ContentBlock.Paragraph(
                            text = "",
                            style = TextStyle.Normal,
                            size = 16,
                            area = TextArea.Left
                        )
                    }
                }

                RecipeContent(
                    recipeId = recipeId,
                    ingredients = ingredients,
                    cookingSteps = cookingSteps,
                    tips = recipeDto.tips
                )
            } else {
                null
            }
        } catch (e: Exception) {

            val contentEntity = localDataSource.getRecipeContent(recipeId) ?: return null
            val ingredients = getIngredientsForRecipe(recipeId)
            RecipeContent(
                recipeId = recipeId,
                ingredients = ingredients,
                cookingSteps = contentEntity.cookingSteps,
                tips = contentEntity.tips
            )
        }
    }



    override suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            val recipesDto = remoteDataSource.searchRecipesForUser(query, userId)
            recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {

            val recipeEntities = localDataSource.searchRecipes(query)
            recipeEntities.map { entity ->
                val categories = getCategoriesForRecipe(entity.id)
                val ingredients = getIngredientsForRecipe(entity.id)
                Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    categories = categories.map { it.toDomain() },
                    ingredients = ingredients,
                    author = entity.author,
                    previewImageUrl = entity.previewImageUrl,
                    cookingTimeMinutes = entity.cookingTimeMinutes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    likesCount = entity.likesCount,
                    isFavorite = isRecipeFavorite(userId,entity.id),
                    isLiked = isRecipeLiked(userId,entity.id)
                )
            }
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<Recipe> {
        return try {
            val recipesDto = remoteDataSource.getRecipesByCategoryForUser(categoryId, userId)
            recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {

            val recipeEntities = localDataSource.getRecipesByCategory(categoryId)
            recipeEntities.map { entity ->
                val categories = getCategoriesForRecipe(entity.id)
                val ingredients = getIngredientsForRecipe(entity.id)
                Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    categories = categories.map { it.toDomain() },
                    ingredients = ingredients,
                    author = entity.author,
                    previewImageUrl = entity.previewImageUrl,
                    cookingTimeMinutes = entity.cookingTimeMinutes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    likesCount = entity.likesCount,
                    isFavorite = isRecipeFavorite(userId, entity.id),
                    isLiked = isRecipeLiked(userId,entity.id)
                )
            }
        }
    }

    override suspend fun getCategories(): List<Category> {
        return try {
            val categoriesDto = remoteDataSource.getAllCategories()
            categoriesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {
            localDataSource.getCategories().map { it.toDomain() }
        }
    }

    override suspend fun getGroceries(): List<Grocery> {
        return try {
            val groceriesDto = remoteDataSource.getAllGroceries()
            groceriesDto.map { dto ->
                Grocery(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description
                )
            }
        } catch (e: Exception) {
            localDataSource.getGroceries().map { it.toDomain() }
        }
    }

    override suspend fun getGroceryItems(): List<GroceryItem> {
        return try {
            val itemsDto = remoteDataSource.getAllGroceryItems()
            itemsDto.map { dto ->
                GroceryItem(
                    id = dto.id,
                    groceryId = dto.groceryId,
                    name = dto.name,
                    defaultUnit = dto.defaultUnit
                )
            }
        } catch (e: Exception) {
            localDataSource.getGroceryItems().map { it.toDomain() }
        }
    }



    override suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<Recipe> {
        return try {
            val recipesDto = remoteDataSource.getRecipesByGroceryItemsForUser(groceryItemIds, userId)
            recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {

            val recipeEntities = localDataSource.getRecipesByGroceryItems(groceryItemIds)
            recipeEntities.map { entity ->
                val categories = getCategoriesForRecipe(entity.id)
                val ingredients = getIngredientsForRecipe(entity.id)
                Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    categories = categories.map { it.toDomain() },
                    ingredients = ingredients,
                    author = entity.author,
                    previewImageUrl = entity.previewImageUrl,
                    cookingTimeMinutes = entity.cookingTimeMinutes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    likesCount = entity.likesCount,
                    isFavorite = isRecipeFavorite(userId,entity.id),
                    isLiked = isRecipeLiked(userId,entity.id)
                )
            }
        }
    }

    override suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<Recipe> {
        return try {
            val recipesDto = remoteDataSource.getRecipesByExactGroceryItemsForUser(groceryItemIds, userId)
            recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {
            val recipeEntities = localDataSource.getRecipesByExactGroceryItems(groceryItemIds)
            recipeEntities.map { entity ->
                val categories = getCategoriesForRecipe(entity.id)
                val ingredients = getIngredientsForRecipe(entity.id)
                Recipe(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    categories = categories.map { it.toDomain() },
                    ingredients = ingredients,
                    author = entity.author,
                    previewImageUrl = entity.previewImageUrl,
                    cookingTimeMinutes = entity.cookingTimeMinutes,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    likesCount = entity.likesCount,
                    isFavorite = isRecipeFavorite(userId,entity.id),
                    isLiked = isRecipeLiked(userId,entity.id)
                )
            }
        }
    }

    override suspend fun getRecipesWithMissingItems(groceryItemIds: List<Int>): List<Recipe> {
        if (groceryItemIds.isEmpty()) return emptyList()

        val allRecipes = getRecipes()
        val userId = this.userId

        return allRecipes.filter { recipe ->
            try {
                val fullRecipe = remoteDataSource.getRecipeByIdForUser(recipe.id, userId)
                val ingredientIds = fullRecipe?.ingredients?.map { it.groceryItem.id } ?: emptyList()
                val missingCount = ingredientIds.count { it !in groceryItemIds }
                missingCount in 1..3
            } catch (e: Exception) {
                false
            }
        }
    }



    override suspend fun getFavoriteRecipes(userId: Int): List<Recipe> {
        return try {
            val favoritesDto = remoteDataSource.getFavouriteRecipes(userId)
            favoritesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {
            val favorites = localDataSource.getFavorites(userId)
            val favoriteRecipeIds = favorites.map { it.recipeId }
            val allRecipes = getRecipes()
            return allRecipes.filter { it.id in favoriteRecipeIds }.map { it.copy(isFavorite = true) }
        }

    }

    override suspend fun addToFavorites(userId: Int, recipeId: Int): Favourite {
        remoteDataSource.addToFavourites(recipeId, userId)
        //localDataSource.addFavorite(userId, recipeId)
        return Favourite(userId, recipeId)
    }

    override suspend fun removeFromFavorites(userId: Int, recipeId: Int): Boolean =
        //localDataSource.removeFavorite(userId, recipeId)
        remoteDataSource.removeFromFavourites(recipeId, userId)

    override suspend fun isRecipeFavorite(userId: Int, recipeId: Int): Boolean {
        return try {
            remoteDataSource.isRecipeFavourite(recipeId, userId)
        } catch (e: Exception) {
            localDataSource.isFavorite(userId, recipeId)
        }
    }

    override suspend fun isRecipeLiked(userId: Int, recipeId: Int): Boolean {
        return try {
            remoteDataSource.isRecipeLiked(recipeId, userId)
        } catch (e: Exception) {
            localDataSource.isLiked(userId, recipeId)
        }
    }


    override suspend fun getLikedRecipes(userId: Int): List<Recipe> {
        val likedRecipes = localDataSource.getUserLikes(userId)
        val likedRecipeIds = likedRecipes.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in likedRecipeIds }.map { it.copy(isLiked = true) }
    }

    override suspend fun addLike(userId: Int, recipeId: Int): Like {
        remoteDataSource.addLike(recipeId, userId)
        //localDataSource.addLike(userId, recipeId)
        return Like(userId, recipeId)
    }

    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean =
        //localDataSource.removeLike(userId, recipeId)
        remoteDataSource.removeLike(recipeId, userId)


    override suspend fun getLikesCount(recipeId: Int): Int {
        return try {
            remoteDataSource.getLikesCount(recipeId)
        } catch (e: Exception) {
            localDataSource.getLikesCount(recipeId)
        }
    }



    private fun parseTextStyle(style: String?): TextStyle {
        return when (style?.lowercase()) {
            "bold" -> TextStyle.Bold
            "italic" -> TextStyle.Italic
            "underlined" -> TextStyle.Underlined
            else -> TextStyle.Normal
        }
    }

    private fun parseTextArea(area: String?): TextArea {
        return when (area?.lowercase()) {
            "center" -> TextArea.Center
            "right" -> TextArea.Right
            else -> TextArea.Left
        }
    }

    //Пока заглушки

    override suspend fun getRecipeByGrocery(groceryId: Int): List<Recipe> = emptyList()
    override suspend fun getGroceryItemById(groceryItemId: Int): GroceryItem? = null

    //Списки покупок пока локально

    override suspend fun getShoppingLists(userId: Int): List<ShoppingList> {
        val listEntities = localDataSource.getShoppingLists(userId)
        return listEntities.map { listEntity ->
            val itemEntities = localDataSource.getShoppingListItems(listEntity.id)
            listEntity.toDomain(itemEntities)
        }
    }

    override suspend fun getShoppingListById(listId: Int): ShoppingList? {
        val listEntity = localDataSource.getShoppingListById(listId) ?: return null
        val itemEntities = localDataSource.getShoppingListItems(listId)
        return listEntity.toDomain(itemEntities)
    }

    override suspend fun createShoppingList(userId: Int, name: String, recipeId: Int?): ShoppingList {
        val listEntity = localDataSource.createShoppingList(userId, name)
        return listEntity.toDomain(emptyList())
    }

    override suspend fun updateShoppingListName(listId: Int, newName: String): ShoppingList? {
        val updatedEntity = localDataSource.updateShoppingListName(listId, newName)
        if (updatedEntity != null) {
            val itemEntities = localDataSource.getShoppingListItems(listId)
            return updatedEntity.toDomain(itemEntities)
        }
        return null
    }

    override suspend fun deleteShoppingList(listId: Int): Boolean {
        return localDataSource.deleteShoppingList(listId)
    }

    override suspend fun getShoppingListItems(listId: Int): List<ShoppingListItem> {
        val itemEntities = localDataSource.getShoppingListItems(listId)
        return itemEntities.map { it.toDomain() }
    }

    override suspend fun addItemToList(listId: Int, item: ShoppingListItem): ShoppingListItem {
        val newItemEntity = localDataSource.addShoppingListItem(listId, item.description, item.quantity, item.unit)
        return newItemEntity.toDomain()
    }

    override suspend fun addItemsFromRecipe(
        listId: Int,
        recipeId: Int,
        ingredients: List<RecipeIngredient>
    ): List<ShoppingListItem> {
        val addedItems = mutableListOf<ShoppingListItem>()
        val existingItems = localDataSource.getShoppingListItems(listId)

        for (ingredient in ingredients) {
            val productName = ingredient.groceryItem.name
            val amount = ingredient.amount
            val unit = ingredient.unit

            val existing = existingItems.find {
                it.description.equals(productName, ignoreCase = true)
            }

            if (existing != null) {
                val existingQuantity = existing.quantity ?: 0.0
                val newQuantity = existingQuantity + amount

                val updated = localDataSource.updateShoppingListItemDetails(
                    existing.id,
                    existing.description,
                    newQuantity,
                    unit
                )
                updated?.let { addedItems.add(it.toDomain()) }
            } else {
                val newItem = localDataSource.addShoppingListItem(
                    listId = listId,
                    description = productName,
                    quantity = amount,
                    unit = unit
                )
                addedItems.add(newItem.toDomain())
            }
        }

        return addedItems
    }

    override suspend fun updateShoppingListItem(itemId: Int, isChecked: Boolean): ShoppingListItem? {
        val updatedEntity = localDataSource.updateShoppingListItem(itemId, isChecked)
        return updatedEntity?.toDomain()
    }

    override suspend fun updateShoppingListItemDetails(
        itemId: Int,
        description: String,
        quantity: Double?,
        unit: String?
    ): ShoppingListItem? {
        return null
    }

    override suspend fun removeShoppingListItem(itemId: Int): Boolean {
        return localDataSource.deleteShoppingListItem(itemId)
    }

    override suspend fun mergeShoppingLists(targetListId: Int, sourceListIds: List<Int>): ShoppingList? {
        val mergedEntity = localDataSource.mergeShoppingLists(targetListId, sourceListIds)
        if (mergedEntity != null) {
            val itemEntities = localDataSource.getShoppingListItems(targetListId)
            return mergedEntity.toDomain(itemEntities)
        }
        return null
    }

    override suspend fun clearCompletedItems(listId: Int): Boolean {
        return localDataSource.clearCompletedItems(listId)
    }
}