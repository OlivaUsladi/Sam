package com.example.myapplication.di.recipes

import com.example.domain.Recipes.use_case.*
import org.koin.dsl.module

val recipesDomainModule = module {
    factory { GetRecipesUseCase(get()) }
    factory { GetRecipeByIdUseCase(get()) }
    factory { GetRecipeContentUseCase(get()) }
    factory { SearchRecipesUseCase(get()) }
    factory { GetRecipesByCategoryUseCase(get()) }
    factory { GetRecipesByGroceryUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetGroceriesUseCase(get()) }

    factory { GetFavoriteRecipesUseCase(get()) }
    factory { AddToFavoritesUseCase(get()) }
    factory { RemoveFromFavoritesUseCase(get()) }
    factory { IsRecipeFavoriteUseCase(get()) }

    factory { GetLikedRecipesUseCase(get()) }
    factory { AddLikeUseCase(get()) }
    factory { RemoveLikeUseCase(get()) }
    factory { IsRecipeLikedUseCase(get()) }
    factory { GetLikesCountUseCase(get()) }

    factory { GetShoppingListsUseCase(get()) }
    factory { GetShoppingListByIdUseCase(get()) }
    factory { CreateShoppingListUseCase(get()) }
    factory { UpdateShoppingListNameUseCase(get()) }
    factory { DeleteShoppingListUseCase(get()) }
    factory { GetShoppingListItemsUseCase(get()) }
    factory { AddItemToListUseCase(get()) }
    factory { AddItemsFromRecipeUseCase(get()) }
    factory { ToggleShoppingListItemUseCase(get()) }
    factory { UpdateShoppingListItemDetailsUseCase(get()) }
    factory { RemoveShoppingListItemUseCase(get()) }
    factory { MergeShoppingListsUseCase(get()) }
    factory { ClearCompletedItemsUseCase(get()) }
}