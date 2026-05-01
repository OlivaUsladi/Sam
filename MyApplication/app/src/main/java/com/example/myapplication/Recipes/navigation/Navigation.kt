package com.example.myapplication.Recipes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.Recipes.ui.category.CategoriesScreen
import com.example.myapplication.Recipes.ui.categoryrecipes.CategoryRecipesScreen
import com.example.myapplication.Recipes.ui.grocery.GroceriesScreen
import com.example.myapplication.Recipes.ui.groceryrecipes.GroceryRecipeScreen
import com.example.myapplication.Recipes.ui.home.RecipeHomeScreen


sealed class Routes(val route: String) {

    object Home : Routes("home")
    object Recipe : Routes("recipe")
    object Category: Routes("category")
    object Grocery: Routes("grocery")
    object CategoryRecipes : Routes("categoryRecipes")
    object GroceryRecipes: Routes("groceryRecipes")
    object Favourite : Routes("favourite")
}

@Composable
fun RecipeNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Home.route) {
            RecipeHomeScreen(navController)
        }

        composable("${Routes.Recipe.route}/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toInt() ?: 0
            //Recipe(articleId = articleId)
        }

        composable(Routes.Grocery.route) {
            GroceriesScreen(navController)
        }

        composable(Routes.Category.route) {
            CategoriesScreen(navController)
        }

        composable("${Routes.GroceryRecipes.route}/{selectedItems}/{onlyGroceries}") { backStackEntry ->
            val selectedItems = backStackEntry.arguments?.getString("selectedItems") ?: ""
            val onlyGroceries = backStackEntry.arguments?.getString("onlyGroceries") ?: "false"
            GroceryRecipeScreen(
                navController = navController,
                selectedItemsStr = selectedItems,
                onlyGroceriesStr = onlyGroceries
            )
        }


        composable("${Routes.CategoryRecipes.route}/{categoryId}/{categoryName}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 1
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Категория"
            CategoryRecipesScreen(
                navController = navController,
                categoryId = categoryId,
                categoryName = categoryName
            )
        }

        composable(Routes.Favourite.route) {
            //FavouriteArticlesScreen(navController)
        }
    }
}