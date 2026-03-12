package com.example.myapplication.Hints.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.Hints.ui.article.ArticleScreen
import com.example.myapplication.Hints.ui.category.CategoriesScreen
import com.example.myapplication.Hints.ui.categoryarticles.CategoryArticlesScreen
import com.example.myapplication.Hints.ui.favourite.FavouriteArticlesScreen
import com.example.myapplication.Hints.ui.home.HomeScreen

sealed class Routes(val route: String) {

    object Home : Routes("home")
    object Article : Routes("article")
    object Category: Routes("category")
    object CategoryArticles : Routes("categoryArticles")
    object Favourite : Routes("favourite")
}

@Composable
fun HintsNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable("${Routes.Article.route}/{articleId}") { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId")?.toInt() ?: 0
            ArticleScreen(articleId = articleId)
        }

        composable(Routes.Category.route) {
            CategoriesScreen(navController)
        }

        composable("${Routes.CategoryArticles.route}/{categoryId}/{categoryName}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 1
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Категория"
            CategoryArticlesScreen(
                navController = navController,
                categoryId = categoryId,
                categoryName = categoryName
            )
        }

        composable(Routes.Favourite.route) {
            FavouriteArticlesScreen(navController)
        }
    }
}