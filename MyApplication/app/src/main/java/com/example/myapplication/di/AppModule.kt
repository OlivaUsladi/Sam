package com.example.myapplication.di

import com.example.myapplication.Hints.ui.home.HomeViewModel
import com.example.myapplication.Hints.ui.article.ArticleViewModel
import com.example.myapplication.Hints.ui.category.CategoriesViewModel
import com.example.myapplication.Hints.ui.categoryarticles.CategoryArticlesViewModel
import com.example.myapplication.Hints.ui.favourite.FavouriteViewModel
import com.example.myapplication.Recipes.ui.home.RecipeHomeViewModel
import com.example.myapplication.Recipes.ui.category.RecipeCategoriesViewModel
import com.example.myapplication.Recipes.ui.categoryrecipes.CategoryRecipesViewModel
import com.example.myapplication.Recipes.ui.favourite.FavouriteRecipeViewModel
import com.example.myapplication.Recipes.ui.grocery.GroceriesViewModel
import com.example.myapplication.Recipes.ui.groceryrecipes.GroceryRecipeViewModel
import com.example.myapplication.Recipes.ui.recipe.RecipeScreenViewModel
import com.example.myapplication.Recipes.ui.shoppinglist.ShoppingListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { HomeViewModel(
        getArticlesUseCase = get(),
        getArticlesByCategoryUseCase = get(),
        searchArticlesUseCase = get(),
        addToFavoriteUseCase = get(),
        removeFromFavoriteUseCase = get(),
        isArticleFavoriteUseCase = get(),
        addLikeUseCase = get(),
        removeLikeUseCase = get(),
        getLikesCountUseCase = get(),
        isArticleLikedUseCase = get()
    ) }

    viewModel { ArticleViewModel(
        getArticleContentUseCase = get(),
        addToFavoriteUseCase = get(),
        removeFromFavoriteUseCase = get(),
        isArticleFavoriteUseCase = get(),
        addLikeUseCase = get(),
        removeLikeUseCase = get(),
        isArticleLikedUseCase = get(),
        getLikesCountUseCase = get()
    ) }

    viewModel { CategoriesViewModel(
        getCategoriesUseCase = get()
    ) }

    viewModel { (categoryId: Int, categoryName: String) ->
        CategoryArticlesViewModel(
            categoryId = categoryId,
            categoryName = categoryName,
            getArticlesByCategoryUseCase = get(),
            searchArticlesUseCase = get(),
            addToFavoriteUseCase = get(),
            removeFromFavoriteUseCase = get(),
            isArticleFavoriteUseCase = get(),
            addLikeUseCase = get(),
            removeLikeUseCase = get(),
            isArticleLikedUseCase = get(),
            getLikesCountUseCase = get()
        )
    }

    viewModel { FavouriteViewModel(
        getFavoriteArticlesUseCase = get(),
        removeFromFavoriteUseCase = get(),
        addLikeUseCase = get(),
        removeLikeUseCase = get(),
        getLikesCountUseCase = get()
    ) }

    viewModel{
        RecipeHomeViewModel(
            getRecipesUseCase = get(),
            searchRecipesUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            isRecipeFavoriteUseCase = get(),
            addLikeUseCase = get(),
            removeLikeUseCase = get(),
            getLikesCountUseCase = get(),
            isRecipeLikedUseCase = get()
        )

    }

    viewModel{
        RecipeCategoriesViewModel(
            getCategoriesUseCase = get()
        )
    }

    viewModel{
        GroceriesViewModel(
            getGroceriesUseCase = get(),
            getGroceryItemsUseCase = get()
        )
    }

    viewModel{
        GroceryRecipeViewModel(
            getRecipesByExactGroceryItemsUseCase = get(),
            getRecipesWithMissingItemsUseCase = get(),
            getRecipesByGroceryItemsUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            isRecipeFavoriteUseCase = get(),
            addLikeUseCase = get(),
            removeLikeUseCase = get(),
            isRecipeLikedUseCase = get(),
            getLikesCountUseCase = get()
        )
    }

    viewModel { (categoryId: Int, categoryName: String) ->
        CategoryRecipesViewModel(
            categoryId = categoryId,
            categoryName = categoryName,
            getRecipesByCategoryUseCase = get(),
            searchRecipesUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            isRecipeFavoriteUseCase = get(),
            addLikeUseCase = get(),
            removeLikeUseCase = get(),
            isRecipeLikedUseCase = get(),
            getLikesCountUseCase = get()
        )
    }

    viewModel { (recipeId: Int) ->
        RecipeScreenViewModel(
            recipeId = recipeId,
            getRecipeByIdUseCase = get(),
            getRecipeContentUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            isRecipeFavoriteUseCase = get(),
            addLikeUseCase = get(),
            removeLikeUseCase = get(),
            isRecipeLikedUseCase = get(),
            getLikesCountUseCase = get()
        )
    }

    viewModel { FavouriteRecipeViewModel(
        getFavoriteRecipesUseCase = get(),
        removeFromFavoritesUseCase = get(),
        addLikeUseCase = get(),
        removeLikeUseCase = get(),
        isRecipeLikedUseCase = get(),
        getLikesCountUseCase = get()
    ) }

    viewModel {
        ShoppingListViewModel(
            getShoppingListsUseCase = get(),
            createShoppingListUseCase = get(),
            updateShoppingListNameUseCase = get(),
            deleteShoppingListUseCase = get(),
            toggleShoppingListItemUseCase = get(),
            removeShoppingListItemUseCase = get(),
            addItemToListUseCase = get(),
            mergeShoppingListsUseCase = get(),
            getGroceryItemsUseCase = get()
        )
    }
}