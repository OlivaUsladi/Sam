package com.example.data.Recipes.datasource.local

import com.example.data.Recipes.model.*
import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.TextArea
import com.example.domain.Recipes.model.TextStyle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDateTime

class RecipeLocalDataSourceImpl : RecipeLocalDataSource {



    private val categories = listOf(
        CategoryEntity(1, "Первые блюда", "Супы, борщи, бульоны"),
        CategoryEntity(2, "Вторые блюда", "Мясные, рыбные, овощные блюда"),
        CategoryEntity(3, "Салаты", "Холодные и горячие закуски"),
        CategoryEntity(4, "Десерты", "Сладкая выпечка и десерты"),
        CategoryEntity(5, "Напитки", "Коктейли, компоты, смузи"),
        CategoryEntity(6, "Выпечка", "Пироги, булочки, хлеб")
    )


    private val groceries = listOf(
        GroceryEntity(1, "Мясо", "Говядина, свинина, курица"),
        GroceryEntity(2, "Овощи", "Свежие, замороженные"),
        GroceryEntity(3, "Крупы и макароны", "Рис, гречка, спагетти"),
        GroceryEntity(4, "Молочные продукты", "Молоко, сыр, сливки"),
        GroceryEntity(5, "Яйца и специи", "Яйца, соль, перец, сахар"),
        GroceryEntity(6, "Масла", "Растительное, оливковое, сливочное"),
        GroceryEntity(7, "Соусы", "Томатная паста, соусы"),
        GroceryEntity(8, "Сладости", "Шоколад, орехи"),
        GroceryEntity(9, "Фрукты", "Яблоки, вишня"),
        GroceryEntity(10, "Зелень", "Укроп, петрушка, базилик"),
        GroceryEntity(11, "Хлеб", "Белый хлеб"),
        GroceryEntity(12, "Бекон", "Бекон")
    )


    private val groceryItems = listOf(
        GroceryItemEntity(1, 1, "Говядина", "г"),
        GroceryItemEntity(2, 1, "Свинина", "г"),
        GroceryItemEntity(3, 1, "Курица", "г"),
        GroceryItemEntity(4, 2, "Свекла", "г"),
        GroceryItemEntity(5, 2, "Картофель", "г"),
        GroceryItemEntity(6, 2, "Морковь", "г"),
        GroceryItemEntity(7, 2, "Капуста", "г"),
        GroceryItemEntity(8, 2, "Лук", "г"),
        GroceryItemEntity(9, 2, "Чеснок", "зубчик"),
        GroceryItemEntity(10, 2, "Тыква", "г"),
        GroceryItemEntity(11, 2, "Помидоры", "г"),
        GroceryItemEntity(12, 3, "Гречка", "г"),
        GroceryItemEntity(13, 3, "Рис", "г"),
        GroceryItemEntity(14, 3, "Спагетти", "г"),
        GroceryItemEntity(15, 3, "Мука", "г"),
        GroceryItemEntity(16, 4, "Молоко", "мл"),
        GroceryItemEntity(17, 4, "Сливки", "мл"),
        GroceryItemEntity(18, 4, "Сметана", "г"),
        GroceryItemEntity(19, 4, "Масло сливочное", "г"),
        GroceryItemEntity(20, 4, "Сыр пармезан", "г"),
        GroceryItemEntity(21, 4, "Моцарелла", "г"),
        GroceryItemEntity(22, 4, "Мороженое", "г"),
        GroceryItemEntity(23, 5, "Яйца", "шт"),
        GroceryItemEntity(24, 5, "Соль", "ч.л."),
        GroceryItemEntity(25, 5, "Перец", "ч.л."),
        GroceryItemEntity(26, 5, "Сахар", "г"),
        GroceryItemEntity(27, 5, "Корица", "ч.л."),
        GroceryItemEntity(28, 5, "Ванильный экстракт", "ч.л."),
        GroceryItemEntity(29, 5, "Разрыхлитель", "г"),
        GroceryItemEntity(30, 5, "Дрожжи", "г"),
        GroceryItemEntity(31, 5, "Какао-порошок", "ст.л."),
        GroceryItemEntity(32, 6, "Масло растительное", "ст.л."),
        GroceryItemEntity(33, 6, "Масло оливковое", "ст.л."),
        GroceryItemEntity(34, 7, "Томатная паста", "ст.л."),
        GroceryItemEntity(35, 7, "Томатный соус", "г"),
        GroceryItemEntity(36, 7, "Соус Цезарь", "мл"),
        GroceryItemEntity(37, 8, "Темный шоколад", "г"),
        GroceryItemEntity(38, 8, "Грецкие орехи", "г"),
        GroceryItemEntity(39, 8, "Сахарная пудра", "ст.л."),
        GroceryItemEntity(40, 8, "Шоколадная крошка", "г"),
        GroceryItemEntity(41, 9, "Яблоки", "г"),
        GroceryItemEntity(42, 9, "Вишня", "шт"),
        GroceryItemEntity(43, 10, "Укроп", "пучок"),
        GroceryItemEntity(44, 10, "Петрушка", "пучок"),
        GroceryItemEntity(45, 10, "Базилик", "пучок"),
        GroceryItemEntity(46, 11, "Белый хлеб", "г"),
        GroceryItemEntity(47, 12, "Бекон", "г")
    )


    private val recipes = listOf(
        RecipeEntity(1, "Борщ с пампушками", "Традиционный украинский борщ...", "Елена Иванова", "https://images.unsplash.com/photo-1547592180-2f1a1b3c3b3a?w=500", 90, LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(10), 45),
        RecipeEntity(2, "Цезарь с курицей", "Классический салат Цезарь...", "Алексей Петров", "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=500", 25, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(5), 32),
        RecipeEntity(3, "Паста Карбонара", "Итальянская паста с беконом...", "Мария Смирнова", "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=500", 30, LocalDateTime.now().minusDays(25), LocalDateTime.now().minusDays(7), 67),
        RecipeEntity(4, "Шоколадный брауни", "Влажный шоколадный пирог...", "Дмитрий Соколов", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500", 45, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(2), 89),
        RecipeEntity(5, "Овощной суп-пюре", "Легкий суп из тыквы...", "Анна Кузнецова", "https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=500", 40, LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(12), 23),
        RecipeEntity(6, "Гречка по-купечески", "Гречневая каша с мясом...", "Иван Петров", "https://images.unsplash.com/photo-1586201375761-83865001e8ac?w=500", 50, LocalDateTime.now().minusDays(18), LocalDateTime.now().minusDays(3), 18),
        RecipeEntity(7, "Пицца Маргарита", "Тонкое тесто, моцарелла...", "Павел Орлов", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=500", 35, LocalDateTime.now().minusDays(22), LocalDateTime.now().minusDays(8), 56),
        RecipeEntity(8, "Салат Оливье", "Классический новогодний салат...", "Татьяна Морозова", "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=500", 60, LocalDateTime.now().minusDays(40), LocalDateTime.now().minusDays(15), 34),
        RecipeEntity(9, "Молочный коктейль", "Освежающий коктейль...", "Сергей Волков", "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=500", 10, LocalDateTime.now().minusDays(12), LocalDateTime.now().minusDays(1), 12),
        RecipeEntity(10, "Яблочный пирог", "Нежный пирог с яблоками...", "Ольга Новикова", "https://images.unsplash.com/photo-1600335895229-6e755c92e4a7?w=500", 70, LocalDateTime.now().minusDays(28), LocalDateTime.now().minusDays(9), 41)
    )



    private val recipeCategories = listOf(
        RecipeCategoryCrossEntity(1, 1), RecipeCategoryCrossEntity(2, 3),
        RecipeCategoryCrossEntity(3, 2), RecipeCategoryCrossEntity(4, 4),
        RecipeCategoryCrossEntity(5, 1), RecipeCategoryCrossEntity(6, 2),
        RecipeCategoryCrossEntity(7, 2), RecipeCategoryCrossEntity(7, 6),
        RecipeCategoryCrossEntity(8, 3), RecipeCategoryCrossEntity(9, 5),
        RecipeCategoryCrossEntity(10, 4), RecipeCategoryCrossEntity(10, 6)
    )



    private val recipeGroceryItemsCrossRef = listOf(
        RecipeGroceryItemCrossEntity(1, 1, 500.0, "г"),
        RecipeGroceryItemCrossEntity(1, 4, 300.0, "г"),
        RecipeGroceryItemCrossEntity(1, 5, 400.0, "г"),
        RecipeGroceryItemCrossEntity(1, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(1, 7, 300.0, "г"),
        RecipeGroceryItemCrossEntity(1, 8, 150.0, "г"),
        RecipeGroceryItemCrossEntity(1, 9, 3.0, "зубчик"),
        RecipeGroceryItemCrossEntity(1, 34, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(1, 43, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(1, 18, 100.0, "г"),
        RecipeGroceryItemCrossEntity(1, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(1, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(2, 3, 400.0, "г"),
        RecipeGroceryItemCrossEntity(2, 46, 100.0, "г"),
        RecipeGroceryItemCrossEntity(2, 11, 150.0, "г"),
        RecipeGroceryItemCrossEntity(2, 20, 50.0, "г"),
        RecipeGroceryItemCrossEntity(2, 23, 2.0, "шт"),
        RecipeGroceryItemCrossEntity(2, 36, 100.0, "мл"),
        RecipeGroceryItemCrossEntity(2, 9, 1.0, "зубчик"),
        RecipeGroceryItemCrossEntity(2, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(2, 24, 5.0, "г"),
        RecipeGroceryItemCrossEntity(2, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(3, 14, 400.0, "г"),
        RecipeGroceryItemCrossEntity(3, 47, 200.0, "г"),
        RecipeGroceryItemCrossEntity(3, 23, 4.0, "шт"),
        RecipeGroceryItemCrossEntity(3, 20, 100.0, "г"),
        RecipeGroceryItemCrossEntity(3, 9, 2.0, "зубчик"),
        RecipeGroceryItemCrossEntity(3, 17, 100.0, "мл"),
        RecipeGroceryItemCrossEntity(3, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(3, 44, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(3, 24, 5.0, "г"),
        RecipeGroceryItemCrossEntity(3, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(4, 37, 200.0, "г"),
        RecipeGroceryItemCrossEntity(4, 19, 150.0, "г"),
        RecipeGroceryItemCrossEntity(4, 26, 200.0, "г"),
        RecipeGroceryItemCrossEntity(4, 23, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(4, 15, 100.0, "г"),
        RecipeGroceryItemCrossEntity(4, 31, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(4, 38, 100.0, "г"),
        RecipeGroceryItemCrossEntity(4, 28, 1.0, "ч.л."),
        RecipeGroceryItemCrossEntity(5, 10, 500.0, "г"),
        RecipeGroceryItemCrossEntity(5, 5, 300.0, "г"),
        RecipeGroceryItemCrossEntity(5, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(5, 8, 150.0, "г"),
        RecipeGroceryItemCrossEntity(5, 17, 200.0, "мл"),
        RecipeGroceryItemCrossEntity(5, 9, 2.0, "зубчик"),
        RecipeGroceryItemCrossEntity(5, 33, 2.0, "ст.л."),
        RecipeGroceryItemCrossEntity(5, 24, 8.0, "г"),
        RecipeGroceryItemCrossEntity(5, 25, 3.0, "г"),
        RecipeGroceryItemCrossEntity(6, 12, 300.0, "г"),
        RecipeGroceryItemCrossEntity(6, 2, 400.0, "г"),
        RecipeGroceryItemCrossEntity(6, 8, 200.0, "г"),
        RecipeGroceryItemCrossEntity(6, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(6, 9, 3.0, "зубчик"),
        RecipeGroceryItemCrossEntity(6, 34, 1.0, "ст.л."),
        RecipeGroceryItemCrossEntity(6, 32, 3.0, "ст.л."),
        RecipeGroceryItemCrossEntity(6, 44, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(6, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(7, 15, 500.0, "г"),
        RecipeGroceryItemCrossEntity(7, 30, 7.0, "г"),
        RecipeGroceryItemCrossEntity(7, 16, 300.0, "мл"),
        RecipeGroceryItemCrossEntity(7, 33, 3.0, "ст.л."),
        RecipeGroceryItemCrossEntity(7, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(7, 26, 5.0, "г"),
        RecipeGroceryItemCrossEntity(7, 35, 150.0, "г"),
        RecipeGroceryItemCrossEntity(7, 21, 250.0, "г"),
        RecipeGroceryItemCrossEntity(7, 45, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(8, 5, 400.0, "г"),
        RecipeGroceryItemCrossEntity(8, 6, 200.0, "г"),
        RecipeGroceryItemCrossEntity(8, 23, 4.0, "шт"),
        RecipeGroceryItemCrossEntity(8, 2, 300.0, "г"),
        RecipeGroceryItemCrossEntity(8, 43, 1.0, "пучок"),
        RecipeGroceryItemCrossEntity(8, 24, 10.0, "г"),
        RecipeGroceryItemCrossEntity(8, 25, 5.0, "г"),
        RecipeGroceryItemCrossEntity(9, 16, 300.0, "мл"),
        RecipeGroceryItemCrossEntity(9, 22, 200.0, "г"),
        RecipeGroceryItemCrossEntity(9, 40, 20.0, "г"),
        RecipeGroceryItemCrossEntity(9, 42, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(10, 15, 250.0, "г"),
        RecipeGroceryItemCrossEntity(10, 19, 150.0, "г"),
        RecipeGroceryItemCrossEntity(10, 26, 200.0, "г"),
        RecipeGroceryItemCrossEntity(10, 23, 3.0, "шт"),
        RecipeGroceryItemCrossEntity(10, 41, 500.0, "г"),
        RecipeGroceryItemCrossEntity(10, 29, 10.0, "г"),
        RecipeGroceryItemCrossEntity(10, 27, 1.0, "ч.л."),
        RecipeGroceryItemCrossEntity(10, 39, 2.0, "ст.л.")
    )



    private val recipeContents = listOf(
        RecipeContentEntity(1, listOf(
            ContentBlock.Paragraph("Приготовление борща", TextStyle.Bold, 24, TextArea.Center),
            ContentBlock.Paragraph("Шаг 1: Варим бульон", TextStyle.Bold, 18, TextArea.Left)
        ), "Подавайте со сметаной"),
        RecipeContentEntity(2, listOf(), "Гренки добавляйте перед подачей"),
        RecipeContentEntity(3, listOf(), null),
        RecipeContentEntity(4, listOf(), null),
        RecipeContentEntity(5, listOf(), null),
        RecipeContentEntity(6, listOf(), null),
        RecipeContentEntity(7, listOf(), null),
        RecipeContentEntity(8, listOf(), null),
        RecipeContentEntity(9, listOf(), null),
        RecipeContentEntity(10, listOf(), null)
    )

    private val favorites = mutableListOf<FavoriteEntity>()
    private val likes = mutableListOf<LikeEntity>()

    private val mutex = Mutex()
    private var nextFavoriteId = 1
    private var nextLikeId = 1



    override suspend fun getRecipes(): List<RecipeEntity> = recipes
    override suspend fun getRecipeById(recipeId: Int): RecipeEntity? = recipes.find { it.id == recipeId }
    override suspend fun getCategories(): List<CategoryEntity> = categories
    override suspend fun getCategoryById(categoryId: Int): CategoryEntity? = categories.find { it.id == categoryId }
    override suspend fun getGroceries(): List<GroceryEntity> = groceries
    override suspend fun getGroceryById(groceryId: Int): GroceryEntity? = groceries.find { it.id == groceryId }
    override suspend fun getGroceryItems(): List<GroceryItemEntity> = groceryItems
    override suspend fun getGroceryItemById(groceryItemId: Int): GroceryItemEntity? = groceryItems.find { it.id == groceryItemId }
    override suspend fun getRecipeGroceryItemIds(recipeId: Int): List<Int> = recipeGroceryItemsCrossRef.filter { it.recipeId == recipeId }.map { it.groceryItemId }
    override suspend fun getRecipeGroceryItemsCrossRef(recipeId: Int): List<RecipeGroceryItemCrossEntity> = recipeGroceryItemsCrossRef.filter { it.recipeId == recipeId }
    override suspend fun getRecipeContent(recipeId: Int): RecipeContentEntity? = recipeContents.find { it.recipeId == recipeId }
    override suspend fun getAllRecipeGroceryItemCross(): List<RecipeGroceryItemCrossEntity> = recipeGroceryItemsCrossRef

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeEntity> {
        val recipeIds = recipeCategories.filter { it.categoryId == categoryId }.map { it.recipeId }
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun getRecipesByGrocery(groceryId: Int): List<RecipeEntity> = emptyList()

    override suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity> {
        val recipeIds = recipeGroceryItemsCrossRef
            .filter { it.groceryItemId in groceryItemIds }
            .map { it.recipeId }
            .distinct()
        return recipes.filter { it.id in recipeIds }
    }

    override suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity> {
        return recipes.filter { recipe ->
            val recipeItemIds = recipeGroceryItemsCrossRef
                .filter { it.recipeId == recipe.id }
                .map { it.groceryItemId }
            recipeItemIds.all { it in groceryItemIds }
        }
    }

    override suspend fun searchRecipes(query: String): List<RecipeEntity> =
        recipes.filter { it.title.contains(query, ignoreCase = true) || it.description?.contains(query, ignoreCase = true) == true }

    override suspend fun getRecipeCategoryIds(recipeId: Int): List<Int> = recipeCategories.filter { it.recipeId == recipeId }.map { it.categoryId }
    override suspend fun getRecipeGroceryIds(recipeId: Int): List<Int> = emptyList()
    //override suspend fun getAllRecipeCategoryCross(): List<RecipeCategoryCrossEntity> = recipeCategories


    override suspend fun getFavorites(userId: Int): List<FavoriteEntity> = favorites.filter { it.userId == userId }
    override suspend fun addFavorite(userId: Int, recipeId: Int): FavoriteEntity = mutex.withLock {
        if (favorites.any { it.userId == userId && it.recipeId == recipeId }) throw IllegalStateException("Уже в Избранном")
        val favorite = FavoriteEntity(nextFavoriteId++, userId, recipeId)
        favorites.add(favorite)
        favorite
    }
    override suspend fun removeFavorite(userId: Int, recipeId: Int): Boolean = mutex.withLock { favorites.removeAll { it.userId == userId && it.recipeId == recipeId } }
    override suspend fun isFavorite(userId: Int, recipeId: Int): Boolean = favorites.any { it.userId == userId && it.recipeId == recipeId }

    override suspend fun getLikes(recipeId: Int): List<LikeEntity> = likes.filter { it.recipeId == recipeId }
    override suspend fun getUserLikes(userId: Int): List<LikeEntity> = likes.filter { it.userId == userId }
    override suspend fun addLike(userId: Int, recipeId: Int): LikeEntity = mutex.withLock {
        if (likes.any { it.userId == userId && it.recipeId == recipeId }) throw IllegalStateException("Уже лайкнуто")
        val like = LikeEntity(nextLikeId++, userId, recipeId)
        likes.add(like)
        val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
        if (recipeIndex != -1) (recipes as MutableList)[recipeIndex] = recipes[recipeIndex].copy(likesCount = recipes[recipeIndex].likesCount + 1)
        like
    }
    override suspend fun removeLike(userId: Int, recipeId: Int): Boolean = mutex.withLock {
        val removed = likes.removeAll { it.userId == userId && it.recipeId == recipeId }
        if (removed) {
            val recipeIndex = recipes.indexOfFirst { it.id == recipeId }
            if (recipeIndex != -1) (recipes as MutableList)[recipeIndex] = recipes[recipeIndex].copy(likesCount = maxOf(0, recipes[recipeIndex].likesCount - 1))
        }
        removed
    }
    override suspend fun isLiked(userId: Int, recipeId: Int): Boolean = likes.any { it.userId == userId && it.recipeId == recipeId }
    override suspend fun getLikesCount(recipeId: Int): Int = likes.count { it.recipeId == recipeId }
}