package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.*
import com.example.domain.Recipes.repository.RecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecipesUseCasesTest {

    private val recipeRepo: RecipeRepository = mock()

    @Test
    fun `GetRecipesUseCase returns all`() = runTest {
        val recs = listOf(mock<Recipe>(), mock<Recipe>())
        whenever(recipeRepo.getRecipes()).thenReturn(recs)
        assertEquals(recs, GetRecipesUseCase(recipeRepo).invoke())
    }

    @Test
    fun `GetRecipeByIdUseCase delegates`() = runTest {
        val r = mock<Recipe>()
        whenever(recipeRepo.getRecipeById(5)).thenReturn(r)
        assertEquals(r, GetRecipeByIdUseCase(recipeRepo).invoke(5))
    }

    @Test
    fun `GetRecipeContentUseCase delegates`() = runTest {
        val content = mock<RecipeContent>()
        whenever(recipeRepo.getRecipeContent(5)).thenReturn(content)
        assertEquals(content, GetRecipeContentUseCase(recipeRepo).invoke(5))
    }

    @Test
    fun `GetRecipesByCategoryUseCase delegates`() = runTest {
        whenever(recipeRepo.getRecipesByCategory(2)).thenReturn(listOf())
        GetRecipesByCategoryUseCase(recipeRepo).invoke(2)
        verify(recipeRepo).getRecipesByCategory(2)
    }

    @Test
    fun `SearchRecipesUseCase delegates`() = runTest {
        whenever(recipeRepo.searchRecipes("борщ")).thenReturn(listOf())
        SearchRecipesUseCase(recipeRepo).invoke("борщ")
        verify(recipeRepo).searchRecipes("борщ")
    }

    @Test
    fun `GetCategoriesUseCase delegates`() = runTest {
        whenever(recipeRepo.getCategories()).thenReturn(listOf())
        GetCategoriesUseCase(recipeRepo).invoke()
        verify(recipeRepo).getCategories()
    }

    @Test
    fun `AddToFavoritesUseCase delegates`() = runTest {
        whenever(recipeRepo.addToFavorites(1, 5)).thenReturn(mock<Favourite>())
        AddToFavoritesUseCase(recipeRepo).invoke(1, 5)
        verify(recipeRepo).addToFavorites(1, 5)
    }

    @Test
    fun `RemoveFromFavoritesUseCase delegates`() = runTest {
        whenever(recipeRepo.removeFromFavorites(1, 5)).thenReturn(true)
        RemoveFromFavoritesUseCase(recipeRepo).invoke(1, 5)
        verify(recipeRepo).removeFromFavorites(1, 5)
    }

    @Test
    fun `IsRecipeFavoriteUseCase returns flag`() = runTest {
        whenever(recipeRepo.isRecipeFavorite(1, 5)).thenReturn(true)
        assertTrue(IsRecipeFavoriteUseCase(recipeRepo).invoke(1, 5))
    }

    @Test
    fun `AddLikeUseCase delegates`() = runTest {
        whenever(recipeRepo.addLike(1, 5)).thenReturn(mock<Like>())
        AddLikeUseCase(recipeRepo).invoke(1, 5)
        verify(recipeRepo).addLike(1, 5)
    }

    @Test
    fun `RemoveLikeUseCase delegates`() = runTest {
        whenever(recipeRepo.removeLike(1, 5)).thenReturn(true)
        RemoveLikeUseCase(recipeRepo).invoke(1, 5)
        verify(recipeRepo).removeLike(1, 5)
    }

    @Test
    fun `IsRecipeLikedUseCase returns flag`() = runTest {
        whenever(recipeRepo.isRecipeLiked(1, 5)).thenReturn(false)
        assertFalse(IsRecipeLikedUseCase(recipeRepo).invoke(1, 5))
    }

    @Test
    fun `GetLikesCountUseCase returns count`() = runTest {
        whenever(recipeRepo.getLikesCount(5)).thenReturn(42)
        assertEquals(42, GetLikesCountUseCase(recipeRepo).invoke(5))
    }

    @Test
    fun `GetShoppingListsUseCase delegates`() = runTest {
        whenever(recipeRepo.getShoppingLists(1)).thenReturn(listOf())
        GetShoppingListsUseCase(recipeRepo).invoke(1)
        verify(recipeRepo).getShoppingLists(1)
    }

    @Test
    fun `CreateShoppingListUseCase delegates`() = runTest {
        val list = mock<ShoppingList>()
        whenever(recipeRepo.createShoppingList(1, "Завтрак")).thenReturn(list)
        assertEquals(list, CreateShoppingListUseCase(recipeRepo).invoke(1, "Завтрак"))
    }

    @Test
    fun `UpdateShoppingListNameUseCase delegates`() = runTest {
        whenever(recipeRepo.updateShoppingListName(1, "Новое имя")).thenReturn(mock())
        UpdateShoppingListNameUseCase(recipeRepo).invoke(1, "Новое имя")
        verify(recipeRepo).updateShoppingListName(1, "Новое имя")
    }

    @Test
    fun `DeleteShoppingListUseCase delegates`() = runTest {
        whenever(recipeRepo.deleteShoppingList(1)).thenReturn(true)
        DeleteShoppingListUseCase(recipeRepo).invoke(1)
        verify(recipeRepo).deleteShoppingList(1)
    }

    @Test
    fun `ToggleShoppingListItemUseCase delegates`() = runTest {
        whenever(recipeRepo.updateShoppingListItem(10, true)).thenReturn(mock())
        ToggleShoppingListItemUseCase(recipeRepo).invoke(10, true)
        verify(recipeRepo).updateShoppingListItem(10, true)
    }

    @Test
    fun `RemoveShoppingListItemUseCase delegates`() = runTest {
        whenever(recipeRepo.removeShoppingListItem(10)).thenReturn(true)
        RemoveShoppingListItemUseCase(recipeRepo).invoke(10)
        verify(recipeRepo).removeShoppingListItem(10)
    }

    @Test
    fun `AddItemToListUseCase delegates`() = runTest {
        val item = mock<ShoppingListItem>()
        whenever(recipeRepo.addItemToList(1, item)).thenReturn(item)
        assertEquals(item, AddItemToListUseCase(recipeRepo).invoke(1, item))
    }

    @Test
    fun `MergeShoppingListsUseCase delegates`() = runTest {
        whenever(recipeRepo.mergeShoppingLists(1, listOf(2, 3))).thenReturn(mock())
        MergeShoppingListsUseCase(recipeRepo).invoke(1, listOf(2, 3))
        verify(recipeRepo).mergeShoppingLists(1, listOf(2, 3))
    }

    @Test
    fun `ClearCompletedItemsUseCase delegates`() = runTest {
        whenever(recipeRepo.clearCompletedItems(1)).thenReturn(true)
        ClearCompletedItemsUseCase(recipeRepo).invoke(1)
        verify(recipeRepo).clearCompletedItems(1)
    }

    @Test
    fun `AddItemsFromRecipeUseCase delegates`() = runTest {
        val items = listOf(mock<ShoppingListItem>())
        val ings = listOf(mock<RecipeIngredient>())
        whenever(recipeRepo.addItemsFromRecipe(1, 5, ings)).thenReturn(items)
        assertEquals(items, AddItemsFromRecipeUseCase(recipeRepo).invoke(1, 5, ings))
    }
}