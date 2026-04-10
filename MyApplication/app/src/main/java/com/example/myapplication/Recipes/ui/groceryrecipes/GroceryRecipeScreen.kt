package com.example.myapplication.Recipes.ui.groceryrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.Recipes.components.RecipeCard
import com.example.myapplication.Recipes.navigation.Routes
import com.example.myapplication.Recipes.ui.home.RecipeHomeEvent
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
        selectedItemsStr.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

    val onlyGroceries = onlyGroceriesStr == "true"

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }


    LaunchedEffect(selectedItems, onlyGroceries) {
        if (selectedItems.isNotEmpty()) {
            if (onlyGroceries) {
                viewModel.onEvent(GroceryRecipeEvent.LoadExactMatchRecipes(selectedItems.toList()))
            } else {
                viewModel.onEvent(GroceryRecipeEvent.LoadAnyMatchRecipes(selectedItems.toList()))
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush)
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Назад",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Добавить ингредиенты",
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ошибка: ${uiState.error}", color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                onlyGroceries -> {
                    if (uiState.exactMatchRecipes.isEmpty() && uiState.recipesWithMissing.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Рецептов не найдено",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (uiState.exactMatchRecipes.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Абсолютное совпадение продуктов:",
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }

                                //Вот тут логика как на основном экране
                                items(uiState.exactMatchRecipes) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onFavoriteClick = {
                                        //viewModel.onEvent(RecipeHomeEvent.ToggleFavorite(recipe.id))
                                    },
                                        onLikeClick = {
                                            //viewModel.onEvent(RecipeHomeEvent.ToggleLike(recipe.id))
                                        },
                                        onRecipeClick = {
                                            navController.navigate("${Routes.Recipe.route}/${recipe.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp))
                                }
                            }

                            if (uiState.recipesWithMissing.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Нужно докупить 1-3 продукта",
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                }

                                items(uiState.recipesWithMissing) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onFavoriteClick = {
                                            //viewModel.onEvent(RecipeHomeEvent.ToggleFavorite(recipe.id))
                                        },
                                        onLikeClick = {
                                            //viewModel.onEvent(RecipeHomeEvent.ToggleLike(recipe.id))
                                        },
                                        onRecipeClick = {
                                            navController.navigate("${Routes.Recipe.route}/${recipe.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                else -> {
                    if (uiState.anyMatchRecipes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Рецептов с выбранными продуктами не найдено",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            item {
                                Text(
                                    text = "Рецепты, содержащие выбранные продукты",
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }

                            items(uiState.anyMatchRecipes) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onFavoriteClick = {
                                        //viewModel.onEvent(RecipeHomeEvent.ToggleFavorite(recipe.id))
                                    },
                                    onLikeClick = {
                                        //viewModel.onEvent(RecipeHomeEvent.ToggleLike(recipe.id))
                                    },
                                    onRecipeClick = {
                                        navController.navigate("${Routes.Recipe.route}/${recipe.id}")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
