package com.example.myapplication.Recipes.ui.groceryrecipes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.myapplication.Recipes.ui.grocery.GroceriesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroceryRecipeScreen(
    navController: NavController,
    selectedItemsStr: String,
    onlyGroceriesStr: String,
    viewModel: GroceryRecipeViewModel = koinViewModel()
    ) {
    val selectedItems = if (selectedItemsStr.isEmpty()) {
        emptySet()
    } else {
        selectedItemsStr.split("/").mapNotNull { it.toIntOrNull() }.toSet()
    }

    val onlyGroceries = onlyGroceriesStr == "true"

    LaunchedEffect(selectedItems, onlyGroceries) {
        if (selectedItems.isNotEmpty()) {
            if (onlyGroceries) {
                viewModel.loadExactMatchRecipes(selectedItems.toList())
            } else {
                viewModel.loadRecipesWithMissing(selectedItems.toList())
            }
        }
    }



}