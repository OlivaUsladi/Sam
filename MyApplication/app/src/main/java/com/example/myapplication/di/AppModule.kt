package com.example.myapplication.di

import com.example.myapplication.Auth.ui.forgot.ForgotPasswordViewModel
import com.example.myapplication.Auth.ui.login.LoginViewModel
import com.example.myapplication.Auth.ui.register.RegisterViewModel
import com.example.myapplication.Finance.ui.accounts.AccountsViewModel
import com.example.myapplication.Finance.ui.analytics.AnalyticsViewModel
import com.example.myapplication.Finance.ui.bankreport.BankReportsViewModel
import com.example.myapplication.Finance.ui.goal.GoalDetailViewModel
import com.example.myapplication.Finance.ui.goal.GoalEditViewModel
import com.example.myapplication.Finance.ui.history.HistoryViewModel
import com.example.myapplication.Finance.ui.sources.SourcesViewModel
import com.example.myapplication.Finance.ui.tag.AssignTagTransactionsViewModel
import com.example.myapplication.Finance.ui.tag.TagEditViewModel
import com.example.myapplication.Finance.ui.tagsgoals.TagsGoalsViewModel
import com.example.myapplication.Finance.ui.transaction.TransactionEditViewModel
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
import com.example.myapplication.Profile.ProfileViewModel
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
        getArticleUseCase = get(),
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
            getLikesCountUseCase = get(),
            getShoppingListsUseCase = get(),
            createShoppingListUseCase = get(),
            addItemsFromRecipeUseCase = get()
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

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get(), get()) }

    viewModel { AccountsViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get(), get(), get(), get()) }
    viewModel { SourcesViewModel(get(), get(), get(), get(), get()) }
    viewModel { TransactionEditViewModel(get(), get(), get(), get(),
        get(), get(), get()) }
    viewModel { BankReportsViewModel(get(), get(), get()) }

    viewModel { AnalyticsViewModel(get(), get(), get(), get()) }
    viewModel { TagsGoalsViewModel(get(), get(), get(), get(), get()) }
    viewModel { TagEditViewModel(get(), get(), get(), get(),
        get(), get()) }
    viewModel { AssignTagTransactionsViewModel(get(), get(), get()) }
    viewModel { GoalDetailViewModel(get(), get(), get(), get()) }
    viewModel { GoalEditViewModel(get(), get(), get()) }

    viewModel { ProfileViewModel(get(), get(), get()) }
}