package com.example.myapplication.Hints

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Routes(val route: String) {

    object Home : Routes("home")
    object Article : Routes("article")
    object Favourite : Routes("favourite")
}

@Composable
fun HintsNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            ListOfArticles(navController)
        }
        composable(Routes.Article.route + "/{articleId}") { stackEntry ->
            val articleIdStr = stackEntry.arguments?.getString("articleId")?:"0"
            val articleId = articleIdStr.toInt()?:0
            ArticleScreen(articleId)
        }
        composable(Routes.Favourite.route) {
            //Страница избранных статей
        }
    }
}