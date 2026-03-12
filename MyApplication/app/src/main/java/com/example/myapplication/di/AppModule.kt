package com.example.myapplication.di

import com.example.myapplication.Hints.ui.home.HomeViewModel
import com.example.myapplication.Hints.ui.article.ArticleViewModel
import com.example.myapplication.Hints.ui.category.CategoriesViewModel
import com.example.myapplication.Hints.ui.categoryarticles.CategoryArticlesViewModel
import com.example.myapplication.Hints.ui.favourite.FavouriteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
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
}