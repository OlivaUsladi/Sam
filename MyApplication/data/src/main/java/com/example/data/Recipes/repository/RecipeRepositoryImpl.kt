package com.example.data.Recipes.repository

import com.example.data.Recipes.datasource.local.RecipeLocalDataSource
import com.example.data.Recipes.datasource.local.model.CategoryEntity
import com.example.data.Recipes.datasource.local.room.ShoppingListLocalDataSource
import com.example.data.Recipes.datasource.local.room.model.ShoppingListItemLocalEntity
import com.example.data.Recipes.datasource.local.room.model.ShoppingListLocalEntity
import com.example.data.Recipes.datasource.remote.RecipeRemoteDataSource
import com.example.data.Recipes.datasource.remote.ShoppingListRemoteDataSource
import com.example.data.Recipes.datasource.remote.dto.AddShoppingListItemRequestDto
import com.example.data.Recipes.datasource.remote.dto.RecipeIngredientFromRecipeDto
import com.example.data.Recipes.datasource.remote.dto.UpdateShoppingListItemRequestDto
import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Recipes.datasource.remote.mapper.RecipeNetworkMapper
import com.example.data.Recipes.datasource.remote.mapper.ShoppingListNetworkMapper
import com.example.data.common.network.NetworkMonitor
import com.example.domain.Recipes.model.*
import com.example.domain.Recipes.repository.RecipeRepository
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

class RecipeRepositoryImpl(
    private val localDataSource: RecipeLocalDataSource,
    private val remoteDataSource: RecipeRemoteDataSource,
    private val shoppingListRemoteDataSource: ShoppingListRemoteDataSource,
    private val shoppingLocal: ShoppingListLocalDataSource,
    private val networkMonitor: NetworkMonitor,
    private val tokenStorage: TokenStorage,
) : RecipeRepository {

    private val userId: Int
        get() = tokenStorage.getUserId() ?: 0

    private val localIdCounter = AtomicInteger(-1)
    private fun nextLocalId(): Int = localIdCounter.getAndDecrement()

    private fun ShoppingListLocalEntity.toDomain(items: List<ShoppingListItemLocalEntity>): ShoppingList =
        ShoppingList(
            id = id,
            userId = userId,
            name = name,
            createdAt = runCatching { LocalDateTime.parse(createdAt) }.getOrDefault(LocalDateTime.now()),
            items = items.map { it.toDomain() }.toMutableList(),
            isCompleted = isCompleted,
        )

    private fun ShoppingListItemLocalEntity.toDomain(): ShoppingListItem =
        ShoppingListItem(
            id = id, description = description,
            isChecked = isChecked, quantity = quantity, unit = unit,
        )

    private fun ShoppingList.toLocalEntity(): ShoppingListLocalEntity =
        ShoppingListLocalEntity(
            id = id, userId = userId, name = name,
            createdAt = createdAt.toString(),
            isCompleted = isCompleted,
        )

    private fun ShoppingListItem.toLocalEntity(listId: Int): ShoppingListItemLocalEntity =
        ShoppingListItemLocalEntity(
            id = id, listId = listId, description = description,
            isChecked = isChecked, quantity = quantity, unit = unit,
        )



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
        val recipesDto = remoteDataSource.getAllRecipesForUser(userId)
        if (recipesDto.isNotEmpty()) {
            return recipesDto.map { RecipeNetworkMapper.mapToDomain(it) }
        }
        val recipeEntities = localDataSource.getRecipes()
        return recipeEntities.map { entity ->
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
                isLiked = isRecipeLiked(userId, entity.id)
            )
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
        @Suppress("NAME_SHADOWING") val userId = this.userId
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
        @Suppress("NAME_SHADOWING") val userId = this.userId
        remoteDataSource.addToFavourites(recipeId, userId)
        //localDataSource.addFavorite(userId, recipeId)
        return Favourite(userId, recipeId)
    }

    override suspend fun removeFromFavorites(userId: Int, recipeId: Int): Boolean {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        //localDataSource.removeFavorite(userId, recipeId)
        return remoteDataSource.removeFromFavourites(recipeId, userId)
    }

    override suspend fun isRecipeFavorite(userId: Int, recipeId: Int): Boolean {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        return try {
            remoteDataSource.isRecipeFavourite(recipeId, userId)
        } catch (e: Exception) {
            localDataSource.isFavorite(userId, recipeId)
        }
    }

    override suspend fun isRecipeLiked(userId: Int, recipeId: Int): Boolean {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        return try {
            remoteDataSource.isRecipeLiked(recipeId, userId)
        } catch (e: Exception) {
            localDataSource.isLiked(userId, recipeId)
        }
    }


    override suspend fun getLikedRecipes(userId: Int): List<Recipe> {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        val likedRecipes = localDataSource.getUserLikes(userId)
        val likedRecipeIds = likedRecipes.map { it.recipeId }
        val allRecipes = getRecipes()
        return allRecipes.filter { it.id in likedRecipeIds }.map { it.copy(isLiked = true) }
    }

    override suspend fun addLike(userId: Int, recipeId: Int): Like {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        remoteDataSource.addLike(recipeId, userId)
        //localDataSource.addLike(userId, recipeId)
        return Like(userId, recipeId)
    }

    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        //localDataSource.removeLike(userId, recipeId)
        return remoteDataSource.removeLike(recipeId, userId)
    }


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


    override suspend fun getRecipeByGrocery(groceryId: Int): List<Recipe> = emptyList()
    override suspend fun getGroceryItemById(groceryItemId: Int): GroceryItem? = null



    override suspend fun getShoppingLists(userId: Int): List<ShoppingList> {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        runCatching {
            val fresh = shoppingListRemoteDataSource.getShoppingLists(userId)
                .map { ShoppingListNetworkMapper.mapToDomain(it) }
            shoppingLocal.replaceAllSyncedLists(userId, fresh.map { it.toLocalEntity() })
            fresh.forEach { sl ->
                val items = sl.items.map { it.toLocalEntity(sl.id) }
                shoppingLocal.deleteAllItemsOfList(sl.id)
                if (items.isNotEmpty()) shoppingLocal.upsertItems(items)
            }
        }

        return shoppingLocal.getLists(userId).map { listEntity ->
            val items = shoppingLocal.getItems(listEntity.id)
            listEntity.toDomain(items)
        }
    }

    override suspend fun getShoppingListById(listId: Int): ShoppingList? {
        return try {
            ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.getShoppingListById(listId, userId)
            )
        } catch (e: Exception) {
            val listEntity = shoppingLocal.findList(listId) ?: return null
            val items = shoppingLocal.getItems(listId)
            listEntity.toDomain(items)
        }
    }

    override suspend fun createShoppingList(userId: Int, name: String, recipeId: Int?): ShoppingList {
        @Suppress("NAME_SHADOWING") val userId = this.userId
        val localId = nextLocalId()
        val nowStr = LocalDateTime.now().toString()
        val pendingRow = ShoppingListLocalEntity(
            id = localId, userId = userId, name = name,
            createdAt = nowStr, isCompleted = false, pendingCreate = true,
        )
        shoppingLocal.upsertList(pendingRow)
        return try {
            val server = ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.createShoppingList(userId, name)
            )
            shoppingLocal.deleteList(localId)
            shoppingLocal.upsertList(server.toLocalEntity())
            server
        } catch (e: Exception) {
            pendingRow.toDomain(emptyList())
        }
    }

    override suspend fun updateShoppingListName(listId: Int, newName: String): ShoppingList? {
        val current = shoppingLocal.findList(listId)
        if (current != null) {
            shoppingLocal.upsertList(current.copy(
                name = newName,
                pendingUpdate = !current.pendingCreate,
            ))
        }
        return try {
            val server = ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.renameShoppingList(listId, userId, newName)
            )
            shoppingLocal.upsertList(server.toLocalEntity())
            server
        } catch (e: Exception) {
            current?.copy(name = newName)?.toDomain(shoppingLocal.getItems(listId))
        }
    }

    override suspend fun deleteShoppingList(listId: Int): Boolean {
        val current = shoppingLocal.findList(listId)
        if (current != null) {
            if (current.pendingCreate) shoppingLocal.deleteList(listId)
            else shoppingLocal.upsertList(current.copy(pendingDelete = true))
        }
        return try {
            val ok = shoppingListRemoteDataSource.deleteShoppingList(listId, userId)
            if (ok) shoppingLocal.deleteList(listId)
            ok
        } catch (e: Exception) {
            true
        }
    }

    override suspend fun getShoppingListItems(listId: Int): List<ShoppingListItem> {
        return try {
            val fresh = shoppingListRemoteDataSource.getShoppingListById(listId, userId).items
                .map { ShoppingListNetworkMapper.mapToDomain(it) }
            shoppingLocal.deleteAllItemsOfList(listId)
            if (fresh.isNotEmpty()) shoppingLocal.upsertItems(fresh.map { it.toLocalEntity(listId) })
            fresh
        } catch (e: Exception) {
            shoppingLocal.getItems(listId).map { it.toDomain() }
        }
    }

    override suspend fun addItemToList(listId: Int, item: ShoppingListItem): ShoppingListItem {
        val localId = nextLocalId()
        val pendingItem = ShoppingListItemLocalEntity(
            id = localId, listId = listId, description = item.description,
            isChecked = item.isChecked, quantity = item.quantity, unit = item.unit,
            pendingCreate = true,
        )
        shoppingLocal.upsertItem(pendingItem)
        return try {
            val server = ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.addItemToList(
                    listId = listId,
                    userId = userId,
                    body = AddShoppingListItemRequestDto(
                        description = item.description,
                        quantity = item.quantity,
                        unit = item.unit,
                    ),
                )
            )
            shoppingLocal.deleteItem(localId)
            shoppingLocal.upsertItem(server.toLocalEntity(listId))
            server
        } catch (e: Exception) {
            pendingItem.toDomain()
        }
    }

    override suspend fun addItemsFromRecipe(
        listId: Int,
        recipeId: Int,
        ingredients: List<RecipeIngredient>
    ): List<ShoppingListItem> {
        val payload = ingredients.map { ingredient ->
            RecipeIngredientFromRecipeDto(
                name = ingredient.groceryItem.name,
                amount = ingredient.amount,
                unit = ingredient.unit
            )
        }

        return try {
            shoppingListRemoteDataSource.addItemsFromRecipe(
                listId = listId,
                userId = userId,
                recipeId = recipeId,
                ingredients = payload
            ).map { ShoppingListNetworkMapper.mapToDomain(it) }
        } catch (e: Exception) {
            val added = mutableListOf<ShoppingListItem>()
            val existing = shoppingLocal.getItems(listId)
            for (ing in ingredients) {
                val name = ing.groceryItem.name
                val ex = existing.find { it.description.equals(name, ignoreCase = true) }
                if (ex != null) {
                    val newQty = (ex.quantity ?: 0.0) + ing.amount
                    val upd = ex.copy(
                        quantity = newQty, unit = ing.unit,
                        pendingUpdate = !ex.pendingCreate,
                    )
                    shoppingLocal.upsertItem(upd)
                    added.add(upd.toDomain())
                } else {
                    val newItem = ShoppingListItemLocalEntity(
                        id = nextLocalId(), listId = listId, description = name,
                        isChecked = false, quantity = ing.amount, unit = ing.unit,
                        pendingCreate = true,
                    )
                    shoppingLocal.upsertItem(newItem)
                    added.add(newItem.toDomain())
                }
            }
            added
        }
    }

    override suspend fun updateShoppingListItem(itemId: Int, isChecked: Boolean): ShoppingListItem? {
        val current = shoppingLocal.findItem(itemId)
        if (current != null) {
            shoppingLocal.upsertItem(current.copy(
                isChecked = isChecked,
                pendingUpdate = !current.pendingCreate,
            ))
        }
        return try {
            val server = ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.updateItem(
                    itemId = itemId,
                    userId = userId,
                    body = UpdateShoppingListItemRequestDto(isChecked = isChecked),
                )
            )
            shoppingLocal.upsertItem(server.toLocalEntity(current?.listId ?: 0))
            server
        } catch (e: Exception) {
            current?.copy(isChecked = isChecked)?.toDomain()
        }
    }

    override suspend fun updateShoppingListItemDetails(
        itemId: Int,
        description: String,
        quantity: Double?,
        unit: String?,
    ): ShoppingListItem? {
        val current = shoppingLocal.findItem(itemId)
        if (current != null) {
            shoppingLocal.upsertItem(current.copy(
                description = description, quantity = quantity, unit = unit,
                pendingUpdate = !current.pendingCreate,
            ))
        }
        return try {
            val server = ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.updateItem(
                    itemId = itemId,
                    userId = userId,
                    body = UpdateShoppingListItemRequestDto(
                        description = description,
                        quantity = quantity,
                        unit = unit,
                    ),
                )
            )
            shoppingLocal.upsertItem(server.toLocalEntity(current?.listId ?: 0))
            server
        } catch (e: Exception) {
            current?.copy(description = description, quantity = quantity, unit = unit)?.toDomain()
        }
    }

    override suspend fun removeShoppingListItem(itemId: Int): Boolean {
        val current = shoppingLocal.findItem(itemId)
        if (current != null) {
            if (current.pendingCreate) shoppingLocal.deleteItem(itemId)
            else shoppingLocal.upsertItem(current.copy(pendingDelete = true))
        }
        return try {
            val ok = shoppingListRemoteDataSource.deleteItem(itemId, userId)
            if (ok) shoppingLocal.deleteItem(itemId)
            ok
        } catch (e: Exception) {
            true
        }
    }

    override suspend fun mergeShoppingLists(targetListId: Int, sourceListIds: List<Int>): ShoppingList? {
        return try {
            ShoppingListNetworkMapper.mapToDomain(
                shoppingListRemoteDataSource.mergeShoppingLists(targetListId, sourceListIds, userId)
            )
        } catch (e: Exception) {
            val target = shoppingLocal.findList(targetListId) ?: return null
            val existingItems = shoppingLocal.getItems(targetListId).toMutableList()
            for (sid in sourceListIds) {
                shoppingLocal.getItems(sid).forEach { item ->
                    val match = existingItems.find {
                        it.description.equals(item.description, ignoreCase = true) &&
                                it.unit.equals(item.unit, ignoreCase = true)
                    }
                    if (match != null) {
                        val merged = match.copy(
                            quantity = (match.quantity ?: 0.0) + (item.quantity ?: 0.0),
                            pendingUpdate = !match.pendingCreate,
                        )
                        shoppingLocal.upsertItem(merged)
                        existingItems[existingItems.indexOf(match)] = merged
                        shoppingLocal.deleteItem(item.id)
                    } else {
                        val moved = item.copy(
                            listId = targetListId,
                            pendingUpdate = !item.pendingCreate,
                        )
                        shoppingLocal.upsertItem(moved)
                        existingItems.add(moved)
                    }
                }
                shoppingLocal.findList(sid)?.let { src ->
                    if (src.pendingCreate) shoppingLocal.deleteList(sid)
                    else shoppingLocal.upsertList(src.copy(pendingDelete = true))
                }
            }
            target.toDomain(shoppingLocal.getItems(targetListId))
        }
    }

    override suspend fun clearCompletedItems(listId: Int): Boolean {
        return try {
            val ok = shoppingListRemoteDataSource.clearCompletedItems(listId, userId)
            if (ok) shoppingLocal.clearCompleted(listId)
            ok
        } catch (e: Exception) {
            shoppingLocal.clearCompleted(listId)
            true
        }
    }

    override suspend fun syncShoppingListsNow() {
        if (!networkMonitor.isOnline.value) return

        shoppingLocal.pendingCreateLists(userId).forEach { row ->
            runCatching {
                val server = ShoppingListNetworkMapper.mapToDomain(
                    shoppingListRemoteDataSource.createShoppingList(row.userId, row.name)
                )
                val orphanedItems = shoppingLocal.getItems(row.id)
                shoppingLocal.deleteList(row.id)
                shoppingLocal.upsertList(server.toLocalEntity())
                orphanedItems.forEach { item ->
                    shoppingLocal.upsertItem(item.copy(listId = server.id))
                }
            }
        }
        shoppingLocal.pendingUpdateLists(userId).forEach { row ->
            runCatching {
                shoppingListRemoteDataSource.renameShoppingList(row.id, row.userId, row.name)
                shoppingLocal.upsertList(row.copy(pendingUpdate = false))
            }
        }
        shoppingLocal.pendingDeleteLists(userId).forEach { row ->
            runCatching {
                shoppingListRemoteDataSource.deleteShoppingList(row.id, row.userId)
                shoppingLocal.deleteList(row.id)
            }
        }

        shoppingLocal.pendingCreateItems(userId).forEach { row ->
            runCatching {
                val server = ShoppingListNetworkMapper.mapToDomain(
                    shoppingListRemoteDataSource.addItemToList(
                        listId = row.listId, userId = userId,
                        body = AddShoppingListItemRequestDto(
                            description = row.description,
                            quantity = row.quantity,
                            unit = row.unit,
                        ),
                    )
                )
                shoppingLocal.deleteItem(row.id)
                shoppingLocal.upsertItem(server.toLocalEntity(row.listId))
            }
        }
        shoppingLocal.pendingUpdateItems(userId).forEach { row ->
            runCatching {
                shoppingListRemoteDataSource.updateItem(
                    itemId = row.id, userId = userId,
                    body = UpdateShoppingListItemRequestDto(
                        description = row.description,
                        quantity = row.quantity,
                        unit = row.unit,
                        isChecked = row.isChecked,
                    ),
                )
                shoppingLocal.upsertItem(row.copy(pendingUpdate = false))
            }
        }
        shoppingLocal.pendingDeleteItems(userId).forEach { row ->
            runCatching {
                shoppingListRemoteDataSource.deleteItem(row.id, userId)
                shoppingLocal.deleteItem(row.id)
            }
        }

        runCatching { getShoppingLists(userId) }
    }
}
