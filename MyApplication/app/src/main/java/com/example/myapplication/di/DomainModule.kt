package com.example.myapplication.di

import com.example.domain.Hints.use_case.*
import org.koin.dsl.module

val domainModule = module {

    factory { GetArticlesUseCase(get()) }
    factory { GetArticlesByCategoryUseCase(get()) }
    factory { GetArticleContentUseCase(get()) }
    factory { SearchArticlesUseCase(get()) }


    factory { GetFavoriteArticlesUseCase(get()) }
    factory { AddToFavoriteUseCase(get()) }
    factory { RemoveFromFavoriteUseCase(get()) }
    factory { IsArticleFavoriteUseCase(get()) }


    factory { GetLikedArticlesUseCase(get()) }
    factory { AddLikeUseCase(get()) }
    factory { RemoveLikeUseCase(get()) }
    factory { IsArticleLikedUseCase(get()) }
    factory { GetLikesCountUseCase(get()) }


    factory { GetCategoriesUseCase(get()) }
}